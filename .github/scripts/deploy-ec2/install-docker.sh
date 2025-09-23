#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source "$SCRIPT_DIR/log-func.sh"

set -e

func_check_docker() {
	# check docker cmd
    if command -v docker &> /dev/null; then
        print_success "Docker is already installed."
    else
        print_error "Docker is not installed."
        return 1
    fi

	# check docker-compose cmd
    if command -v docker-compose &> /dev/null; then
        print_success "docker-compose is already installed."
    elif docker compose version &> /dev/null; then
        print_success "docker compose plugin is available."
    else
        print_error "docker-compose is not installed."
        return 2
    fi
	
    return 0
}

func_install_docker_ubuntu() {
    # Update system
    sudo apt-get update -y

    # Install Docker
    sudo apt-get install -y ca-certificates curl gnupg lsb-release
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
    sudo apt-get install docker-compose -y

    # Start Docker service
    sudo systemctl start docker
    sudo systemctl enable docker

    # Add ubuntu user to docker group
    sudo usermod -aG docker ubuntu

	# Apply new group membership
	newgrp docker

    print_success "Docker Engine installation completed!"
	print_info "You may need to log out and log back in for group changes to take effect."
}

# Main execution
func_install_docker() {
    if ! func_check_docker; then
        print_info "Docker not found. Installing Docker..."
        func_install_docker_ubuntu
    fi
}

