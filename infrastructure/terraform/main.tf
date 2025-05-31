terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

# Create namespace
resource "kubernetes_namespace" "screen" {
  metadata {
    name = "screen"
    labels = {
      name = "screen"
    }
  }
}

# Create ConfigMap
resource "kubernetes_config_map" "screen_config" {
  metadata {
    name      = "screen-config"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  data = {
    api_url = "http://gateway-service:8080"
  }
}

# Create database service
resource "kubernetes_service" "db_service" {
  metadata {
    name      = "db-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "db"
    }

    port {
      port        = 5432
      target_port = 5432
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }
}

# Create database deployment
resource "kubernetes_deployment" "db" {
  metadata {
    name      = "db"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "db"
      }
    }

    template {
      metadata {
        labels = {
          app = "db"
        }
      }

      spec {
        container {
          image = "postgres:15"
          name  = "postgres"

          port {
            container_port = 5432
          }

          env {
            name  = "POSTGRES_USER"
            value = var.postgres_user
          }

          env {
            name  = "POSTGRES_PASSWORD"
            value = var.postgres_password
          }

          env {
            name  = "POSTGRES_DB"
            value = var.postgres_db
          }

          volume_mount {
            name       = "postgres-data"
            mount_path = "/var/lib/postgresql/data"
          }
        }

        volume {
          name = "postgres-data"
          empty_dir {}
        }
      }
    }
  }
} 