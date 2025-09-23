#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/../shared/log-func.sh"

# ::::: Environment Variables :::::
export TAG="$TAG"
export PATTERN="$PATTERN"

# ::::: Functions :::::

# Validate the format of a given tag against a specified pattern.
#
# Arguments:
#   $1 (string) - The tag to validate.
#   $2 (string) - The regex pattern to validate the tag against (optional, default: ^v[0-9]+(\.[0-9]+)*$).
#
# Returns:
#   0 - If the tag is valid.
#   1 - If the tag is invalid.
#
# Example:
#   func_validate_tag "v1.0.0" "^v[0-9]+(\.[0-9]+)*$"
func_validate_tag() {
	# Arguments:
	local tag="$1"
	local pattern="${2:-^v[0-9]+(\.[0-9]+)*$}" # vx.y.z (eg. v1.0.0, v2.1.3, ...)

	# Execution:
	if [[ ! "$tag" =~ $pattern ]]; then
		echo "valid=1" >> "$GITHUB_OUTPUT"
        return 1
    fi

	# Set the output variables
	{
		echo "tag=$tag"
		echo "version=${tag%%-*}"
		echo "suffix=${tag/${tag%%-*}/}"
		echo "valid=0"
	} >> "$GITHUB_OUTPUT"
	
    return 0
}

# ::::: Execution :::::
if ! func_validate_tag "$TAG" "$PATTERN"; then
    print_error "Tag validation failed."
    exit 1
fi