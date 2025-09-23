#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$DIR_NAME/log-func.sh"

# :::::: Functions ::::::


# Replace environment variable values in a file based on a list of replacements.
#
# Arguments:
#   $1 - Path to the file to be updated.
#   $2 - Multi-line string containing environment variable assignments (VAR=VALUE).
# Returns:
#   0 if replacements succeed, 1 if there is an error.
# Example:
#   replacements+="\
#       DATABASE__PASSWORD=12345678
#       DATABASE__DATABASE=s-store
#   "
#   func_replace_file_content "./config.env" "$replacements"
func_replace_file_content() {
	# Arguments:
	local file_path=$1
	local replacements="$2"

	# Variables:
	local count=0

	# Read current docker-compose content
	new_file_content=$(cat "$file_path")
	    
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

			# Replace all occurrences of var_name with var_value in new_file_content
			new_file_content=$(func_safe_replace_str "$new_file_content" "$var_name" "$var_value")

            count=$((count + 1))
        fi
    done <<< "$replacements"

	if [ $count -eq 0 ]; then
		print_error "No replacements made on $(basename "$file_path")"
		return 1
	fi

    if echo "$new_file_content" > "$file_path"; then
        print_success "Total $count replacements made on $(basename "$file_path")"
        return 0
    else
        print_error "Failed to replace content in $file_path"
        return 1
    fi

}


##
# Safely replace all occurrences of a pattern with a replacement string in the given content.
# Handles special characters (such as &) to ensure compatibility across different shell environments.
#
# Arguments:
#   $1 - The input string (content) to perform replacement on.
#   $2 - The pattern to search for (substring to be replaced).
#   $3 - The replacement string (can contain special characters).
# Returns:
#   The modified string with all occurrences of pattern replaced by replacement.
# Example:
#   func_safe_replace_str "Hello & World" "World" "Earth & Mars"
#   # Output: Hello & Earth & Mars
func_safe_replace_str() {
	local content="$1"
    local pattern="$2" 
    local replacement="$3"

	# Check if the shell supports direct replacement with &
	if [[ $(tmp="x"; echo "${tmp//x/&}") == "&" ]]; then
		echo "${content//$pattern/$replacement}"
	else
		replacement_escaped="${replacement//&/\\&}" 
		echo "${content//$pattern/$replacement_escaped}"
	fi
}