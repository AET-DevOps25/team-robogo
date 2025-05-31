output "namespace" {
  description = "Created namespace name"
  value       = kubernetes_namespace.screen.metadata[0].name
}

output "db_service_name" {
  description = "Database service name"
  value       = kubernetes_service.db_service.metadata[0].name
}

output "db_service_port" {
  description = "Database service port"
  value       = kubernetes_service.db_service.spec[0].port[0].port
} 