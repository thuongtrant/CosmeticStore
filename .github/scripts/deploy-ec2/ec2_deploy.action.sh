#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$DIR_NAME/../shared/log-func.sh"
source "$DIR_NAME/../shared/env-func.sh"
source "$DIR_NAME/../shared/ssh-func.sh"
source "$DIR_NAME/../constant.sh"
source "$DIR_NAME/../shared/ultils.sh"

# ::::: Functions :::::

func_update_deployment_state() {
	# Arguments:
	local backend_service_version="$1"
	local frontend_service_version="$2"

	# Variables:
	local set_file_as_prefix="true"
	local set_github_var=""
	local state_file_path=".github/state/deployment.txt"
	local state_file_path_absolute="$ROOT_DIR/$state_file_path"

	# Prepare new lines for replacement
	local new_backend_service_version_line="BACKEND_SERVICE_VERSION=$backend_service_version"
	local new_frontend_service_version_line="FRONTEND_SERVICE_VERSION=$frontend_service_version"

	# If state file doesn't exist, create it with new lines
	if [ ! -f "$state_file_path_absolute" ]; then
		echo "\
		$new_backend_service_version_line
		$new_frontend_service_version_line
		" > "$state_file_path_absolute"
		return 0
	fi

	# Prepare old lines for replacement
	func_set_env_by_files "$state_file_path" "" "$set_file_as_prefix" "$set_github_var"
	local old_backend_service_version_line="BACKEND_SERVICE_VERSION=$DEPLOYMENT__BACKEND_SERVICE_VERSION"
	local old_frontend_service_version_line="FRONTEND_SERVICE_VERSION=$DEPLOYMENT__FRONTEND_SERVICE_VERSION"

	# Read current state content
	state="$(cat "$state_file_path_absolute")"

	if [ -n "$backend_service_version" ]; then
		state="${state//$old_backend_service_version_line/$new_backend_service_version_line}"
	fi

	if [ -n "$frontend_service_version" ]; then
		state="${state//$old_frontend_service_version_line/$new_frontend_service_version_line}"
	fi

	# Write back to state file
	if ! echo "$state" > "$state_file_path_absolute"; then
		print_error "Failed to update deployment state file"
		return 1
	else
		if [[ $DEBUG_MODE == "true" ]]; then
			print_info "$(basename "$state_file_path") updated content:"
			echo ""
			cat "$state_file_path_absolute"
		fi
		print_success "Deployment state file updated successfully"
	fi

	return 0
}

func_rewrite_docker_compose() {
	# Arguments:
	local docker_username="$1"
	local docker_compose_path="$2"
	local backend_secrets="$3"
	local frontend_secrets="$4"
	local database_secrets="$5"

	# Variables:
	local set_file_as_prefix="true"
	local set_github_var=""
	local state_file_path=".github/state/deployment.txt"
	local state_file_path_absolute="$ROOT_DIR/$state_file_path"

	if [[ $DEBUG_MODE == "true" && -f "$state_file_path_absolute" ]]; then
		print_info "$(basename "$state_file_path") loaded content:"
		echo ""
		cat "$state_file_path_absolute"
	fi

	# Load Variables:
	func_set_env "$backend_secrets" "BACKEND__" "$set_github_var"
	func_set_env "$frontend_secrets" "FRONTEND__" "$set_github_var"
	func_set_env "$database_secrets" "DATABASE__" "$set_github_var"
	func_set_env_by_files "$state_file_path" "" "$set_file_as_prefix" "$set_github_var"

	# Prepare Docker Image Repositories
	backend_docker_image_repo="$docker_username/$BACKEND__SERVICE_DOCKER_IMAGE_NAME:$DEPLOYMENT__BACKEND_SERVICE_VERSION"
	frontend_docker_image_repo="$docker_username/$FRONTEND__SERVICE_DOCKER_IMAGE_NAME:$DEPLOYMENT__FRONTEND_SERVICE_VERSION"

	replacements="
		BACKEND__DOCKER_IMAGE_REPO=$backend_docker_image_repo
		FRONTEND__DOCKER_IMAGE_REPO=$frontend_docker_image_repo
	"

	if ! func_replace_file_content "$docker_compose_path" "$replacements"; then
		print_error "Failed to update $docker_compose_path"
		return 1
	else
		if [[ $DEBUG_MODE == "true" ]]; then
			print_info "$(basename "$state_file_path") rewrited content:"
			echo ""
			cat "$state_file_path_absolute"
		fi
		print_success "$docker_compose_path updated successfully"
	fi
}

func_transfer_resources_via_scp() {
	# Arguments:
	local ssh_user="$1"
	local ssh_host="$2"
    local file_paths="$3"
	local instance_bash_dir="$4"

	# Variables:

	# Transfer files via SCP
    scp -o StrictHostKeyChecking=no -o ConnectTimeout=10 $file_paths "$ssh_user"@"$ssh_host":"$instance_bash_dir"

	# Check SCP result
    if [ $? -eq 0 ]; then
        return 0
    else
        return 1
    fi
}

func_ssh_deploy() {
    ssh -o StrictHostKeyChecking=no -o ConnectTimeout=10 "$SSH_USER@$SSH_HOST" <<EOF
export SSH_USER="$SSH_USER"
export SSH_HOST="$SSH_HOST"

export INSTANCE_BASH_DIR="$INSTANCE_BASH_DIR"
export APP_DIR="$APP_DIR"


export DOCKER_HUB_USERNAME="$DOCKER_HUB_USERNAME"
export DOCKER_HUB_TOKEN="$DOCKER_HUB_TOKEN"
export DOCKER_IMAGE_NAME="$DOCKER_IMAGE_NAME"
export DOCKER_IMAGE_VERSION="$DOCKER_IMAGE_VERSION"
export DOCKER_COMPOSE_PATH="$APP_DIR/$DOCKER_COMPOSE_NAME"

export SERVICE_PORT="$SERVICE_PORT"
export SERVICE_NAME="$SERVICE_NAME"
export HEALTH_CHECK_URL="$HEALTH_CHECK_URL"

cd $INSTANCE_BASH_DIR
bash ./instance-init.sh
EOF

    if [ $? -eq 0 ]; then
        print_success "SSH deployment completed successfully"
        return 0
    else
        print_error "SSH deployment failed"
        return 1
    fi
}

# ::::: Environment Variables :::::
export SSH_USER="$SSH_USER"
export SSH_HOST="$SSH_HOST"
export SSH_IP="$SSH_IP"
export SSH_DNS="$SSH_DNS"
export KEY_PAIR="$KEY_PAIR"

export INSTANCE_BASH_DIR="$INSTANCE_BASH_DIR"
export APP_DIR="$APP_DIR"

export DOCKER_HUB_USERNAME="$DOCKER_HUB_USERNAME"
export DOCKER_HUB_TOKEN="$DOCKER_HUB_TOKEN"
export DOCKER_IMAGE_NAME="$DOCKER_IMAGE_NAME"
export DOCKER_IMAGE_VERSION="$DOCKER_IMAGE_VERSION"
export DOCKER_COMPOSE_NAME="$DOCKER_COMPOSE_NAME"

export SERVICE_PORT="$SERVICE_PORT"
export SERVICE_NAME="$SERVICE_NAME"
export HEALTH_CHECK_URL="$HEALTH_CHECK_URL"

export BACKEND_SECRETS="$BACKEND_SECRETS"
export FRONTEND_SECRETS="$FRONTEND_SECRETS"
export DATABASE_SECRETS="$DATABASE_SECRETS"

# ::::: Execution :::::

# Create SSH key file
print_info "Setup SSH key pair..."

if ! func_setup_ssh_private_key_agent "$KEY_PAIR"; then
    print_error "Failed to setup SSH key pair"
    exit 1
fi


# Update deployment state file with new service versions
print_heading "Updating deployment state file..."
backend_service_version=""
frontend_service_version=""

if [[ "$SERVICE_NAME" == "backend" ]]; then
	backend_service_version="$DOCKER_IMAGE_VERSION"
elif [[ "$SERVICE_NAME" == "frontend" ]]; then
	frontend_service_version="$DOCKER_IMAGE_VERSION"
fi

if ! func_update_deployment_state "$backend_service_version" "$frontend_service_version"; then
	print_error "Failed to update deployment state file"
	exit 1
fi

# Update docker-compose.prod.yml with the new image version
print_heading "Updating docker-compose.prod.yml..."

if ! func_rewrite_docker_compose \
    "$DOCKER_HUB_USERNAME" \
    "$ROOT_DIR/$DOCKER_COMPOSE_NAME" \
    "$BACKEND_SECRETS" \
    "$FRONTEND_SECRETS" \
    "$DATABASE_SECRETS"
then
	print_error "Failed to update $DOCKER_COMPOSE_NAME"
	exit 1
fi

if [[ $DEBUG_MODE == "true" ]]; then
	print_debug "--- Updated $DOCKER_COMPOSE_NAME ---"
	cat "$ROOT_DIR/$DOCKER_COMPOSE_NAME"
fi

# Transfer necessary resources to the EC2 instance

print_heading "Transferring resources to EC2 instance..."

echo "$BACKEND_SERVICE_ENV" > "$ROOT_DIR/.github/backend-service.env"
echo "$DATABASE_SERVICE_ENV" > "$ROOT_DIR/.github/database-service.env"

file_paths="\
$ROOT_DIR/.github/scripts/shared/log-func.sh \
$ROOT_DIR/.github/scripts/deploy-ec2/install-docker.sh \
$ROOT_DIR/.github/scripts/deploy-ec2/instance-init.sh \
$ROOT_DIR/.github/backend-service.env \
$ROOT_DIR/.github/database-service.env \
$ROOT_DIR/$DOCKER_COMPOSE_NAME"

if ! func_transfer_resources_via_scp "$SSH_USER" "$SSH_HOST" "$file_paths" "$INSTANCE_BASH_DIR"; then
    print_error "Failed to transfer resources via SCP"
    exit 1
fi

# Deploy using SSH with proper error handling
print_info "Starting SSH to EC2 instance..."
if ! func_ssh_deploy; then
    print_error "SSH failed"
    exit 1
fi

# Final Output
print_success "Deployment completed successfully!"
echo "🎉 $SERVICE_NAME deployment completed!"
echo "🔗 Service URL IP: http://$SSH_IP:$SERVICE_PORT"
echo "🔗 Service URL DNS: https://$SSH_DNS:$SERVICE_PORT"