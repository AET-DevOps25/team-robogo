variable "environment" {
  description = "Deployment environment (development/production)"
  type        = string
  default     = "development"
}

variable "namespace" {
  description = "Kubernetes namespace"
  type        = string
  default     = "team-robogo"
}

variable "debug" {
  description = "Enable debug mode"
  type        = bool
  default     = false
} 