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

output "gateway_service_name" {
  description = "API Gateway service name"
  value       = kubernetes_service.gateway_service.metadata[0].name
}

output "gateway_service_port" {
  description = "API Gateway service port"
  value       = kubernetes_service.gateway_service.spec[0].port[0].port
}

output "frontend_service_name" {
  description = "Frontend service name"
  value       = kubernetes_service.frontend_service.metadata[0].name
}

output "frontend_service_port" {
  description = "Frontend service port"
  value       = kubernetes_service.frontend_service.spec[0].port[0].port
}

output "backend_service_name" {
  description = "Backend service name"
  value       = kubernetes_service.backend_service.metadata[0].name
}

output "backend_service_port" {
  description = "Backend service port"
  value       = kubernetes_service.backend_service.spec[0].port[0].port
}