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

# Create service for client
resource "kubernetes_service" "client_service" {
  metadata {
    name      = "client-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "client"
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


# Create deployment for client
resource "kubernetes_deployment" "client" {
  metadata {
    name      = "client"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "client"
      }
    }

    template {
      metadata {
        labels = {
          app = "client"
        }
      }

      spec {
        container {
          name  = "client"
          image = "ghcr.io/aet-devops25/team-robogo/client:main"
          port {
            container_port = 80
          }
        }
      }
    }
  }
}

# Create server service
resource "kubernetes_service" "server_service" {
  metadata {
    name      = "server-service"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    selector = {
      app = "server"
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

# Create deployment for server
resource "kubernetes_deployment" "server" {
  metadata {
    name      = "server"
    namespace = kubernetes_namespace.screen.metadata[0].name
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "server"
      }
    }

    template {
      metadata {
        labels = {
          app = "server"
        }
      }

      spec {
        container {
          name  = "server"
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
