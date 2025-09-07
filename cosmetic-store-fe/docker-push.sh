#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/../shared/docker-func.sh"

# Check if version is passed as parameter, otherwise prompt user for input
# Usage: ./docker-push.sh v2.0.0
if [ -z "$1" ]; then
  	print_info "Enter version for the image (e.g., v1.0.0):"
	read -r -p "Version: " VERSION
else
	VERSION="$1"
fi

# Exit if no version is provided
if [ -z "$VERSION" ]; then
  print_error "No version provided, exiting!"
  exit 1
fi

# Push Docker image
image_repo="thuongtrant"
image_name="cosmetic-store-fe"

func_push_docker_image "$image_repo" "$image_name" "$VERSION" "." "Dockerfile"