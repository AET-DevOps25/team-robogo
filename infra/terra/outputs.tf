output "app_namespace" {
  description = "The Kubernetes namespace where the application is deployed."
  value       = kubernetes_namespace.app_namespace.metadata[0].name
}

output "helm_release_status" {
  description = "The status of the Helm release."
  value       = helm_release.app_release.status
}

output "client_service_nodeport" {
  description = "The NodePort for the client service to access the UI."
  # Note: This requires the client service in your Helm chart to be of type NodePort
  # and have a defined nodePort value. We access it from the Helm release resource.
  value = helm_release.app_release.metadata[0].notes
}
