terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
  }
}

# Configure Kubernetes provider
provider "kubernetes" {
  config_path = "~/.kube/config"
  # Select different contexts based on environment
  config_context = var.environment == "production" ? "production-cluster" : "docker-desktop"
}

# Create namespace
resource "kubernetes_namespace" "robogo" {
  metadata {
    name = var.namespace
  }
}

# Create ConfigMap
resource "kubernetes_config_map" "robogo_config" {
  metadata {
    name      = "robogo-config-${var.environment}"
    namespace = kubernetes_namespace.robogo.metadata[0].name
  }

  data = {
    ENVIRONMENT = var.environment
    API_VERSION = "v1"
    DEBUG       = var.debug ? "true" : "false"
    DB_HOST     = var.environment == "production" ? "prod-db.example.com" : "localhost"
    DB_PORT     = "5432"
  }
} 