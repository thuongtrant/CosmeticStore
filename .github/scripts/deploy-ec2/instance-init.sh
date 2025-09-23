#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/log-func.sh"
source "$SCRIPT_DIR/install-docker.sh"

print_heading "Validating Variables..."
func_validate_variable() {
    local var_name="$1"
    local var_value="$2"

    if [[ $DEBUG_MODE == "true" ]]; then
        print_info "$var_name: $var_value"
    fi
    
    if [ -z "$var_value" ]; then
        print_error "Environment variable '$var_name' is not set or is empty."
        exit 1
    fi
}
func_validate_variable "SSH_USER" "$SSH_USER"
func_validate_variable "SSH_HOST" "$SSH_HOST"
func_validate_variable "INSTANCE_BASH_DIR" "$INSTANCE_BASH_DIR"
func_validate_variable "APP_DIR" "$APP_DIR"
func_validate_variable "DOCKER_HUB_USERNAME" "$DOCKER_HUB_USERNAME"
func_validate_variable "DOCKER_HUB_TOKEN" "$DOCKER_HUB_TOKEN"
func_validate_variable "DOCKER_IMAGE_NAME" "$DOCKER_IMAGE_NAME"
func_validate_variable "DOCKER_IMAGE_VERSION" "$DOCKER_IMAGE_VERSION"
func_validate_variable "DOCKER_COMPOSE_PATH" "$DOCKER_COMPOSE_PATH"
func_validate_variable "SERVICE_PORT" "$SERVICE_PORT"
func_validate_variable "SERVICE_NAME" "$SERVICE_NAME"
func_validate_variable "HEALTH_CHECK_URL" "$HEALTH_CHECK_URL"

print_success "All required variables are set."

# ::::: Functions :::::
func_navigate_to_app_dir() {
    local app_dir=$1
    cd "$app_dir" || {
        print_info "Creating application directory..."
        sudo mkdir -p "$app_dir"
        if ! cd "$app_dir"; then
            print_error "Failed to navigate to or create application directory: $app_dir"
            return 1
        fi
    }
    print_success "Navigated to application directory: $app_dir"
    return 0
}

func_docker_login() {
    local username=$1
    local token=$2
    
    if [ -z "$username" ] || [ -z "$token" ]; then
        print_info "Skipping Docker login - credentials not provided"
        return 0
    fi
    
    print_info "Logging into Docker Hub as $username"
    if echo "$token" | docker login --username "$username" --password-stdin; then
        print_success "Successfully logged into Docker Hub"
        return 0
    else
        print_warning "Docker login failed - proceeding with anonymous access"
        return 0  # Continue even if login fails, as image might be public
    fi
}

function_pull_docker_image() {
    local image_repo="$1/$2:$3"

    # Validate image repository format
    if [ -z "$image_repo" ]; then
        print_error "Image repository is empty"
        return 1
    fi
    
    print_info "Attempting to pull image: '$image_repo'"
    local max_retries=3
    local attempt=1
    while [ $attempt -le $max_retries ]; do
        if docker pull "$image_repo"; then
            print_success "Pulled Docker image: $image_repo"
            return 0
        else
            print_error "Attempt $attempt/$max_retries: Failed to pull Docker image: $image_repo"
            if [ $attempt -lt $max_retries ]; then
                print_info "Retrying in 5 seconds..."
                sleep 5
            fi
        fi
        ((attempt++))
    done
    print_error "Failed to pull Docker image after $max_retries attempts: $image_repo"
    print_error "Please check if the image exists and is accessible"
    return 1
}

func_enable_multi_platform() {
    print_info "Setting up Docker buildx for multi-platform support..."
    
    # Install QEMU emulation for ARM64
    if ! docker run --rm --privileged tonistiigi/binfmt --install arm64 2>/dev/null; then
        print_warning "Failed to install QEMU ARM64 emulation, but continuing..."
    fi
    
    # Enable experimental features in Docker daemon if needed (>/dev/null 2>&1]
    if ! docker buildx version 2>/dev/null; then
        print_warning "Docker buildx not available, multi-platform support might be limited"
    else
        print_success "Multi-platform support enabled"
    fi
}

func_start_docker_compose() {
    local docker_compose_path=$1

    # Enable multi-platform support first
    func_enable_multi_platform
    
    if docker compose -f "$docker_compose_path" up -d; then
        print_success "Docker Compose started successfully"
        return 0
    else
        print_error "Failed to start Docker Compose"
        return 1
    fi
}

func_waiting_for_service_to_be_ready() {
    local service_name=$1
    local max_retries=10
    local wait_time=6
    local attempt=1

    print_info "Waiting for $service_name to be ready..."

    while [ $attempt -le $max_retries ]; do
        if docker ps | grep -q "$service_name"; then
            print_success "$service_name is running."
            return 0
        else
            print_info "Attempt $attempt/$max_retries: $service_name not ready yet. Retrying in $wait_time seconds..."
            sleep $wait_time
            ((attempt++))
        fi
    done

    print_error "$service_name failed to start after $max_retries attempts."
    return 1
}

func_verify_service_healthy() {
    local service_name=$1
    local health_check_url=$2
    local max_retries=10
    local wait_time=6
    local attempt=1

    print_info "Verifying $service_name service health at $health_check_url..."

    while [ $attempt -le $max_retries ]; do
        if curl -s --head "$health_check_url" | grep "200" > /dev/null; then
            print_success "$service_name service is healthy and reachable at $health_check_url"
            return 0
        else
            print_info "Attempt $attempt/$max_retries: $service_name service not healthy yet. Retrying in $wait_time seconds..."
            sleep $wait_time
            ((attempt++))
        fi
    done

    print_error "$service_name service failed to become healthy after $max_retries attempts."
    return 1
}

func_show_container_status() {
    local service_name=$1
	local docker_compose_path=$2
    docker-compose -f "$docker_compose_path" ps "$service_name"
}

# ::::: Environment Variables :::::
export SSH_USER="$SSH_USER"
export SSH_HOST="$SSH_HOST"

export INSTANCE_BASH_DIR="$INSTANCE_BASH_DIR"
export APP_DIR="$APP_DIR"

export DOCKER_HUB_USERNAME="$DOCKER_HUB_USERNAME"
export DOCKER_HUB_TOKEN="$DOCKER_HUB_TOKEN"
export DOCKER_IMAGE_NAME="$DOCKER_IMAGE_NAME"
export DOCKER_IMAGE_VERSION="$DOCKER_IMAGE_VERSION"
export DOCKER_COMPOSE_PATH="$DOCKER_COMPOSE_PATH"

export SERVICE_PORT="$SERVICE_PORT"
export SERVICE_NAME="$SERVICE_NAME"
export HEALTH_CHECK_URL="$HEALTH_CHECK_URL"

# ::::: Main Script Execution :::::

print_heading "Connecting to EC2 instance...."
print_success "Connected to EC2 instance: $SSH_USER@$SSH_HOST"

print_heading "Checking Docker installation..."
func_install_docker

print_heading "Checking application directory..."
func_navigate_to_app_dir "$APP_DIR"

# print_heading "Logging into Docker Hub..."
# func_docker_login "$DOCKER_HUB_USERNAME" "$DOCKER_HUB_TOKEN"

print_heading "Pulling Docker image..."
echo "
DOCKER_HUB_USERNAME=$DOCKER_HUB_USERNAME
DOCKER_HUB_TOKEN=$DOCKER_HUB_TOKEN
DOCKER_IMAGE_NAME=$DOCKER_IMAGE_NAME
DOCKER_IMAGE_VERSION=$DOCKER_IMAGE_VERSION
" > "xxx.env" 
function_pull_docker_image "$DOCKER_HUB_USERNAME" "$DOCKER_IMAGE_NAME" "$DOCKER_IMAGE_VERSION"

print_heading "Starting Docker Compose..."
func_start_docker_compose "$DOCKER_COMPOSE_PATH"

print_heading "Waiting for $SERVICE_NAME to be ready..."
func_waiting_for_service_to_be_ready "$SERVICE_NAME"

print_heading "Verifying service health..."
func_verify_service_healthy "$SERVICE_NAME" "$HEALTH_CHECK_URL"

print_heading "Showing container status..."
func_show_container_status "$SERVICE_NAME" "$DOCKER_COMPOSE_PATH"

print_heading "Deployment of $SERVICE_NAME completed successfully!"
