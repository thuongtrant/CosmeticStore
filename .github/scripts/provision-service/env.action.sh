#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/../shared/log-func.sh"
source "$DIR_NAME/../shared/env-func.sh"
source "$DIR_NAME/../constant.sh"

# ::::: Environment Variables :::::
export ENV_FILES="$ENV_FILES"
export ENV_DIR="$ENV_DIR"
export ENV_FILE_AS_PREFIX="${ENV_FILE_AS_PREFIX:-false}"

export ENV_SECRETS="$ENV_SECRETS"
export ENV_SECRETS_PREFIX="${ENV_SECRETS_PREFIX:-}"
export SET_GITHUB_VAR="${SET_GITHUB_VAR:-}"
export DEBUG="${DEBUG:-false}"

# ::::: Functions :::::

# ::::: Execution :::::
if ! func_set_env_by_files "$ENV_FILES" "$ENV_DIR" "$ENV_FILE_AS_PREFIX" "$SET_GITHUB_VAR"; then
	print_error "Load environment files failed."
	exit 1
fi

if ! func_set_env "$ENV_SECRETS" "$ENV_SECRETS_PREFIX" "$SET_GITHUB_VAR"; then
	print_error "Load repository secrets failed."
	exit 1
fi

print_success "DEPLOYMENT__BACKEND_SERVICE_VERSION=$DEPLOYMENT__BACKEND_SERVICE_VERSION"