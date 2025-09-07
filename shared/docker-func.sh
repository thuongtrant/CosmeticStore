#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/console-log.sh"

func_push_docker_image() {
    local image_repo="$1/$2"
    local version=$3
    local build_context=${4:-.}
    local dockerfile=${5:-Dockerfile}

    # Validate required arguments
    if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ]; then
        print_error "Image repo and version are required arguments"
        print_info "Usage: func_push_docker_image <username> <image_name> <version> [build_context] [dockerfile]"
        return 1
    fi

    # Setup Docker Buildx for multi-platform builds
    print_info "Setting up Docker Buildx for multi-platform builds"
    
    # Remove existing builder if it exists
    docker buildx rm multi-platform-builder 2>/dev/null || true
    
    # Create and use new builder
    if docker buildx create --use --name multi-platform-builder --driver docker-container; then
        print_success "Docker Buildx setup completed"
    else
        print_error "Failed to setup Docker Buildx"
        return 1
    fi

    # Build and push multi-platform images directly
    print_info "Building and pushing multi-platform image: $image_repo"
    print_info "Build context: $build_context, Dockerfile: $dockerfile"
    print_info "Platforms: linux/amd64, linux/arm64"
    print_info "Tags: latest, $version"
    
    if docker buildx build \
        --platform linux/amd64,linux/arm64 \
        --push \
        --tag "$image_repo:latest" \
        --tag "$image_repo:$version" \
        -f "$build_context/$dockerfile" \
        "$build_context"; then
        print_success "Successfully built and pushed multi-platform image: $image_repo with tags latest and $version"
    else
        print_error "Failed to build and push multi-platform image"
        return 1
    fi
}
