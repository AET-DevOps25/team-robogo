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

variable "gateway_url" {
  description = "API gateway URL"
  type        = string
  default     = "http://gateway-service:8080/api"
} 

variable "db_url" {
  description = "Database URL"
  type        = string
  default     = "jdbc:postgresql://db-service:5432/robogo_db"
}

variable "server_port" {
  description = "Server port"
  type        = string
  default     = "8081"
}