#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/../shared/log-func.sh"
source "$DIR_NAME/../shared/env-func.sh"
source "$DIR_NAME/../constant.sh"


# ::::: Functions :::::

func_service() {
    # Arguments:
	local version="$1"
	local suffix="$2"
	local set_github_var="${3}"

	# Variables:
	local service_env=""
	local env_prefix=""

	# Set service-specific variables
    if [[ "$suffix" == "-fe" ]]; then
        service_env="$SECRETS_FRONTEND"
    else
        service_env="$SECRETS_BACKEND"
    fi

	# Load environment variables from repository secrets
	func_set_env "$service_env" "$env_prefix" "$set_github_var"

	# Github Outputs
	{	
		echo "name=$SERVICE_NAME"
		echo "short_name=$SERVICE_SHORT_NAME"
		echo "dir=$SERVICE_DIR"
		echo "docker_file_name=$SERVICE_DOCKER_FILE_NAME"
		echo "registry=$SERVICE_REGISTRY"
		echo "docker_image_name=$SERVICE_DOCKER_IMAGE_NAME"
		echo "health_check_url=$SERVICE_HEALTH_CHECK_URL"
		echo "port=$SERVICE_PORT"
		echo "version=$version"
	} >> "$GITHUB_OUTPUT"

}


# ::::: Environment Variables :::::
export TAG="$TAG"
export TAG_VERSION="$TAG_VERSION"
export TAG_SUFFIX="$TAG_SUFFIX"

export SECRETS_BACKEND="$SECRETS_BACKEND"
export SECRETS_FRONTEND="$SECRETS_FRONTEND"

export DEBUG="${DEBUG:-false}"
export SET_GITHUB_VAR="${SET_GITHUB_VAR}"

# ::::: Execution :::::

if ! func_service "$TAG_VERSION" "$TAG_SUFFIX" "$SET_GITHUB_VAR" "$DEBUG"; then
	print_error "Service provisioning failed."
	exit 1
fi