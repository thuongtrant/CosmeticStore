#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/log-func.sh"
source "$DIR_NAME/../constant.sh"


# ::::: Functions :::::


# Load environment variables from a multi-line string and optionally export them, mask secrets, and write to GitHub Actions environment/output files.
#
# Arguments:
#   $1 - env_secrets: Multi-line string containing environment variable assignments (VAR=VALUE).
#   $2 - env_prefix: Prefix to prepend to each variable name (optional).
#   $3 - assign_to_github_var: Options: ENV, OUTPUT, ENV|OUTPUT (controls where to write variables).
# Returns:
#   None. Prints info and sets environment variables as needed.
# Example:
#   func_set_env "$ENV_SECRETS" "APP_" "ENV"
func_set_env() {
    # Arguments:
	local env_secrets="$1"
	local env_prefix="$2"
	local assign_to_github_var="${3}" # Options: ENV, OUTPUT, ENV|OUTPUT

	if [[ $DEBUG_MODE == "true" ]]; then
		print_debug "ENV_SECRETS: ----"
		print_debug "ENV_PREFIX: $env_prefix"
		print_debug "ASSIGN_TO_GITHUB_VAR: $assign_to_github_var"
	fi

	# Variables:
    local count=1
	local mark_value="false"
    
    # Process line by line instead of word by word
    while IFS= read -r line; do
        # Skip empty lines and comments
        if [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]]; then
            continue
        fi
        
        # Check if line is a valid environment variable (allow leading whitespace)
        if [[ "$line" =~ ^[[:space:]]*[A-Za-z_][A-Za-z0-9_]*=.* ]]; then
            # Extract variable name and value
            var_name=$(echo "$line" | cut -d'=' -f1 | xargs)
            var_value=$(echo "$line" | cut -d'=' -f2- | xargs)
            
            # Mask the secret value in GitHub Actions logs
			if [ "$mark_value" = "true" ]; then
				echo "::add-mask::$var_value"
			fi
            
            # Load environment variable in current shell and GitHub Actions environment
            local env_line="$(echo "${env_prefix}" | tr '[:lower:]' '[:upper:]')${var_name}=$var_value"
			export "$env_line"

			# Append to GitHub Actions environment file
			if [[ "$assign_to_github_var" == *ENV* ]]; then
				echo "$env_line" >> $GITHUB_ENV
			fi

			# Append to GitHub Actions output file
			if [[ "$assign_to_github_var" == *OUTPUT* ]]; then
				echo "$env_line" >> $GITHUB_OUTPUT
			fi

            # Print variable to console
			if [ "$DEBUG_MODE" = "true" ]; then
				print_info "$(echo "${env_prefix}" | tr '[:lower:]' '[:upper:]')${var_name} = $var_value"
			fi
            
            count=$((count + 1))
        fi
    done <<< "$env_secrets"

	if [ "$DEBUG_MODE" = "true" ]; then
		print_success "Total $((count - 1)) environment variables loaded successfully."
	fi
}

# Load environment variables from multiple files, optionally add prefix, mask secrets, and write to GitHub Actions environment/output files.
#
# Arguments:
#   $1 - env_relative_file_paths: Space-separated list of environment file names.
#   $2 - env_dir: Directory containing environment files (default: DIR_NAME).
#   $3 - file_name_as_prefix: true/false, whether to use file name as prefix for variables (default: false).
#   $4 - assign_to_github_var: Options: ENV, OUTPUT, ENV|OUTPUT (controls where to write variables).
# Returns:
#   0 if all files loaded successfully, 1 if any file is missing.
# Example:
#   func_set_env_by_files "cloud.env backend.env" ".github/workflows/env" "true" "ENV"
func_set_env_by_files() {
	# Arguments:
	local env_relative_file_paths="$1"
	local env_dir="${2}"
	local file_name_as_prefix="${3:-false}"
	local assign_to_github_var="${4}" # Options: ENV, OUTPUT, ENV|OUTPUT

	# Variables:
	local file_name=""
	local env_prefix=""

	# Loop through each environment file path
    for env_path in $env_relative_file_paths; do
	
		if [ -n "$env_dir" ]; then
			env_path="$env_dir/$env_path"
		else
			env_path="$ROOT_DIR/$env_path"
		fi

        if [ -f "$env_path" ]; then
			file_name=$(basename "$env_path")

			if [ "$file_name_as_prefix" = "true" ]; then
				env_prefix="${file_name%.*}__" # Remove file extension
			fi

            func_set_env "$(cat "$env_path")" "$env_prefix" "$assign_to_github_var"
        else
            print_error "Environment file not found: $env_path"
			return 1
        fi
    done

	return 0
}