variable "namespace" {
  description = "The Kubernetes namespace to deploy into."
  type        = string
  default     = "team-robogo-tf"
}

variable "postgres_user" {
  description = "Username for the PostgreSQL database."
  type        = string
  sensitive   = true
}

variable "postgres_password" {
  description = "Password for the PostgreSQL database."
  type        = string
  sensitive   = true
}

variable "postgres_db" {
  description = "Name of the PostgreSQL database."
  type        = string
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
  description = "Port for the server"
  type        = number
  default     = 8081
}

variable "chair_api_key" {
  description = "API key for the Chair service."
  type        = string
  sensitive   = true
}

variable "openai_api_key" {
  description = "API key for the OpenAI service."
  type        = string
  sensitive   = true
}
