output "namespace" {
  description = "Created namespace name"
  value       = kubernetes_namespace.team_robogo.metadata[0].name
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

output "client_service_name" {
  description = "client service name"
  value       = kubernetes_service.client_service.metadata[0].name
}

output "client_service_port" {
  description = "client service port"
  value       = kubernetes_service.client_service.spec[0].port[0].port
}

output "server_service_name" {
  description = "server service name"
  value       = kubernetes_service.server_service.metadata[0].name
}

output "server_service_port" {
  description = "server service port"
  value       = kubernetes_service.server_service.spec[0].port[0].port
}
