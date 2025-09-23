#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/../shared/log-func.sh"

# ::::: Environment Variables :::::
export SERVICE="$SERVICE"
export VERSION="$VERSION"

# ::::: Functions :::::


func_get_tag_dispatch() {
	# Arguments:
	local service="$1"
	local version="$2"

	# Variables:
	local tag=""
	local tag_suffix=""

	# Execution:
	if [ -z "$service" ] || [ -z "$version" ]; then
		print_error "Service or version is not provided."
		print "Service: $service, Version: $version"
		return 1
	fi

	# Determine tag suffix based on service
	if [ "$service" == "frontend" ]; then
		tag_suffix="fe"
	elif [ "$service" == "backend" ]; then
		tag_suffix="be"
	fi

    # Determine the tag based on version
	if [ "$version" == "latest" ]; then
		git fetch --tags
		tag=$(git tag -l "v*.*.*-${tag_suffix}" | sort -V | tail -n 1)
	else
		tag="${version}-${tag_suffix}"
	fi

	# Set the output variable
	echo "tag=${tag}" >> "$GITHUB_OUTPUT"

    return 0
}

# ::::: Execution :::::
if ! func_get_tag_dispatch "$SERVICE" "$VERSION"; then
    print_error "Get Tag dispatch failed."
	exit 1
fi