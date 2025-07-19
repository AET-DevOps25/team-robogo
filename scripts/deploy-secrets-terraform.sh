#!/bin/bash

# Deploy Kubernetes Secrets using Terraform
# This script uses Terraform to manage secrets in a more secure way

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TERRAFORM_DIR="$PROJECT_ROOT/infra/terraform"
SECRETS_FILE="$TERRAFORM_DIR/secrets.tfvars"

echo "🔐 Deploying Kubernetes Secrets using Terraform..."

# Check if secrets file exists
if [ ! -f "$SECRETS_FILE" ]; then
    echo "❌ Secrets file not found: $SECRETS_FILE"
    echo "📝 Please copy secrets.tfvars.template to secrets.tfvars and fill in your API keys"
    exit 1
fi

# Change to terraform directory
cd "$TERRAFORM_DIR"

# Initialize Terraform (if needed)
if [ ! -d ".terraform" ]; then
    echo "🔧 Initializing Terraform..."
    terraform init
fi

# Plan the deployment
echo "📋 Planning Terraform deployment..."
terraform plan -var-file="secrets.tfvars"

# Apply the changes
echo "🚀 Applying Terraform changes..."
terraform apply -var-file="secrets.tfvars" -auto-approve

echo "✅ Secrets deployed successfully using Terraform!" 