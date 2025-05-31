variable "namespace" {
  description = "Kubernetes namespace name"
  type        = string
  default     = "screen"
}

variable "postgres_user" {
  description = "PostgreSQL username"
  type        = string
  default     = "robogo"
}

variable "postgres_password" {
  description = "PostgreSQL password"
  type        = string
  default     = "robogo_pass"
}

variable "postgres_db" {
  description = "PostgreSQL database name"
  type        = string
  default     = "robogo_db"
}

variable "api_url" {
  description = "API gateway URL"
  type        = string
  default     = "http://gateway-service:8080"
} 