# Cosmetic Store Frontend Deployment Strategy on EC2

## Overview
This document outlines the deployment strategy for cosmetic-store-fe on EC2 using shell commands for Terraform user_data.

## Architecture
- **Frontend**: React 19 application built with Node.js 18
- **Web Server**: Nginx serving static build files
- **Port**: 80 (HTTP)
- **Build Tool**: Create React App with react-scripts

## EC2 Setup Script for Terraform user_data (Ubuntu 24.04)

```bash
#!/bin/bash

# Update system packages
sudo apt update -y

# Install required packages
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common git

# Install Docker
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update -y
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Start and enable Docker
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ubuntu

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Clone repository (replace with your actual repository URL)
cd /home/ubuntu
sudo -u ubuntu git clone <REPOSITORY_URL> cosmetic-store
cd cosmetic-store

# Create environment file for frontend if needed
sudo -u ubuntu tee ./cosmetic-store-fe/.env.production << 'EOF'
REACT_APP_API_BASE_URL=http://localhost:8080
GENERATE_SOURCEMAP=false
EOF

# Change ownership to ubuntu user
sudo chown -R ubuntu:ubuntu /home/ubuntu/cosmetic-store

# Run docker-compose exactly like local development
sudo docker-compose up -d frontend

# Create systemd service for auto-restart on boot
sudo tee /etc/systemd/system/cosmetic-store.service << 'EOF'
[Unit]
Description=Cosmetic Store Application
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
User=root
WorkingDirectory=/home/ubuntu/cosmetic-store
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable cosmetic-store.service

# Wait for container to be ready
sleep 30

echo "Frontend deployment completed successfully!"
echo "Application will be available at http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
echo "Container status:"
sudo docker ps
```

## Alternative: Direct Node.js Deployment (without Docker)

```bash
#!/bin/bash

# Update system packages
sudo yum update -y

# Install Node.js 18.x
curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
sudo yum install -y nodejs

# Install nginx
sudo amazon-linux-extras install -y nginx1
sudo systemctl enable nginx

# Install PM2 for process management
sudo npm install -g pm2

# Create application user
sudo useradd -r -s /bin/false cosmetic

# Create application directory
sudo mkdir -p /opt/cosmetic-store-fe
sudo chown cosmetic:cosmetic /opt/cosmetic-store-fe

# Clone and setup application
cd /opt/cosmetic-store-fe
sudo -u cosmetic git clone <REPOSITORY_URL> .
cd cosmetic-store-fe

# Install dependencies and build
sudo -u cosmetic npm install --legacy-peer-deps
sudo -u cosmetic npm run build

# Configure nginx
sudo tee /etc/nginx/conf.d/cosmetic-frontend.conf << 'EOF'
server {
    listen 80;
    server_name _;
    
    root /opt/cosmetic-store-fe/cosmetic-store-fe/build;
    index index.html;
    
    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/xml+rss
        application/json;
    
    # Handle client-side routing
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
}
EOF

# Remove default nginx configuration
sudo rm -f /etc/nginx/conf.d/default.conf

# Start nginx
sudo systemctl start nginx

echo "Frontend deployment completed successfully!"
echo "Application will be available at http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4)"
```

## Terraform Integration

Add this to your EC2 instance resource in Terraform:

```hcl
resource "aws_instance" "cosmetic_frontend" {
  ami           = "ami-0866a3c8686eaeeba" # Ubuntu 24.04 LTS
  instance_type = "t3.micro"
  
  user_data = file("${path.module}/setup-fe-node.sh")
  
  vpc_security_group_ids = [aws_security_group.frontend.id]
  subnet_id              = aws_subnet.public[0].id
  
  tags = {
    Name = "cosmetic-store-frontend"
  }
}

resource "aws_security_group" "frontend" {
  name_description = "Security group for cosmetic store frontend"
  vpc_id           = aws_vpc.main.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"] # Restrict this to your IP
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "cosmetic-frontend-sg"
  }
}
```

## Environment Configuration

Create environment-specific configurations:

### Production Environment Variables
```bash
REACT_APP_API_BASE_URL=https://your-backend-domain.com
GENERATE_SOURCEMAP=false
REACT_APP_ENVIRONMENT=production
```

### Development Environment Variables
```bash
REACT_APP_API_BASE_URL=http://localhost:8080
GENERATE_SOURCEMAP=true
REACT_APP_ENVIRONMENT=development
```

## Monitoring and Maintenance

1. **Health Check**:
```bash
curl -f http://localhost/ || exit 1
```

2. **Log Monitoring**:
```bash
# For Docker deployment
docker logs cosmetic-frontend

# For direct deployment
sudo journalctl -u nginx -f
```

3. **Auto-deployment Script** (optional):
```bash
#!/bin/bash
cd /opt/cosmetic-store
git pull origin main
docker-compose up -d --build frontend
```

## Security Considerations

1. Use HTTPS in production (configure SSL certificate)
2. Restrict security group rules to necessary ports only
3. Regular security updates: `sudo yum update -y`
4. Consider using AWS Application Load Balancer for SSL termination
5. Enable AWS CloudWatch monitoring

## Cost Optimization

1. Use t3.micro for development (free tier eligible)
2. Use t3.small or t3.medium for production based on traffic
3. Consider using AWS CloudFront CDN for static assets
4. Enable gzip compression to reduce bandwidth costs

## Backup Strategy

1. Regular AMI snapshots of the EC2 instance
2. Code repository backup (GitHub/GitLab)
3. Configuration files backup to S3