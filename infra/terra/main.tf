terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "2.31.0"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "2.14.0"
    }
  }
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "docker-desktop"
}

provider "helm" {
  kubernetes {
    config_path    = "~/.kube/config"
    config_context = "docker-desktop"
  }
}

# Create the namespace for the application
resource "kubernetes_namespace" "app_namespace" {
  metadata {
    name = var.namespace
  }
}

# Create the secret for the GenAI service from variables
resource "kubernetes_secret" "genai_secret" {
  metadata {
    name      = "genai-secret"
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
  }

  data = {
    CHAIR_API_KEY  = var.chair_api_key
    OPENAI_API_KEY = var.openai_api_key
  }

  type = "Opaque"
}

# Create the secret for the Database from variables
resource "kubernetes_secret" "db_secret" {
  metadata {
    name      = "db-credentials"
    namespace = kubernetes_namespace.app_namespace.metadata[0].name
  }

  data = {
    POSTGRES_USER     = var.postgres_user
    POSTGRES_PASSWORD = var.postgres_password
    POSTGRES_DB       = var.postgres_db
  }

  type = "Opaque"
}

# Deploy the application using the local Helm chart
resource "helm_release" "app_release" {
  name       = "robogo-release"
  chart      = "../helm/screen" # Relative path to the chart
  namespace  = kubernetes_namespace.app_namespace.metadata[0].name
  
  # Wait for resources to be ready
  wait = true
  
  # This block is equivalent to the values.yaml file.
  # Values set here will override the ones in the chart's values.yaml.
  values = [
    yamlencode({
      genai = {
        secretName = kubernetes_secret.genai_secret.metadata[0].name
      }
      database = {
        secretName = kubernetes_secret.db_secret.metadata[0].name
      }
      # You can override other values from your chart here if needed
      # For example:
      # client = {
      #   service = {
      #     nodePort = 30001
      #   }
      # }
    })
  ]

  # Ensures the secret is created before the helm chart is deployed
  depends_on = [
    kubernetes_secret.genai_secret,
    kubernetes_secret.db_secret
  ]
}
