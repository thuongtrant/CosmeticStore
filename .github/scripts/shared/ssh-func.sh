#!/bin/bash

DIR_NAME="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# ::::: Source :::::
source "$DIR_NAME/log-func.sh"


# ::::: Functions :::::


# Sets up an SSH private key and adds it to the SSH agent.

# Arguments:
#   $1 - The SSH private key content as a string.
#
# Returns:
#   0 if the setup is successful, 1 if there is an error.
#
# Example:
#   func_setup_ssh_private_key_agent "$SSH_PRIVATE_KEY"
func_setup_ssh_private_key_agent() {
	# Arguments:
    local ssh_private_key_pair=$1

	# Variables:
    local ssh_dir="$HOME/.ssh"
    local key_pair_path="$ssh_dir/key_pair.pem"

	# Create SSH directory if it doesn't exist
    mkdir -p "$ssh_dir"

	# Write key content to file
    echo "$ssh_private_key_pair" > "$key_pair_path"
    chmod 600 "$key_pair_path"

    # Start ssh-agent if not running
    eval "$(ssh-agent -s)"

    # Add key to agent
    ssh-add "$key_pair_path"

	# Check if ssh-add was failed
    if [ $? -eq 1 ]; then
		print_error "Failed to provision SSH key pair or add to agent"
        return 1
    fi

	return 0
}
