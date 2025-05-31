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


// Create api gateway service
resource "kubernetes_service" "gateway_service" {
  metadata {
    name      = "gateway-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "gateway"
    }

    port {
      port        = 8080
      target_port = 8080
      protocol    = "TCP"
    }

    type = "ClusterIP"
  }
}

// Create api gateway deployment
resource "kubernetes_deployment" "gateway" {
  metadata {
    name      = "gateway"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "gateway"
      }
    }

    template {
      metadata {
        labels = {
          app = "gateway"
        }
      }

      spec {
        container {
          name  = "gateway"
          image = "ghcr.io/aet-devops25/team-robogo/api-gateway:main"
          port {
            container_port = 8080
          }
        }
      }
    }
  }
}

# Create service for frontend
resource "kubernetes_service" "frontend_service" {
  metadata {
    name      = "frontend-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "frontend"
    }

    port {
      port        = 80
      target_port = 80
      node_port   = 30000
      protocol    = "TCP"
    }

    type = "NodePort"
  }
}


# Create deployment for frontend
resource "kubernetes_deployment" "frontend" {
  metadata {
    name      = "frontend"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "frontend"
      }
    }

    template {
      metadata {
        labels = {
          app = "frontend"
        }
      }

      spec {
        container {
          name  = "frontend"
          image = "ghcr.io/aet-devops25/team-robogo/client:main"
          port {
            container_port = 80
          }
        }
      }
    }
  }
}

# Create backend service
resource "kubernetes_service" "backend_service" {
  metadata {
    name      = "backend-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "backend"
    }

    port {
      port        = 8081
      target_port = 8081
      node_port   = 30001
      protocol    = "TCP"
    }

    type = "NodePort"
  }
}

# Create deployment for backend
resource "kubernetes_deployment" "backend" {
  metadata {
    name      = "backend"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "backend"
      }
    }

    template {
      metadata {
        labels = {
          app = "backend"
        }
      }

      spec {
        container {
          name  = "backend"
          image = "ghcr.io/aet-devops25/team-robogo/server:main"
          port {
            container_port = 8081
          }

          env {
            name = "SPRING_DATASOURCE_URL"
            value = var.db_url
          }

          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value = var.postgres_user
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value = var.postgres_password
          }

          env {
            name = "SERVER_PORT"
            value = var.server_port
          }
        }
      }
    }
  }

  depends_on = [kubernetes_service.db_service]
}