variable "aws_region" {
  description = "AWS region"
  default     = "us-east-1"
}

variable "aws_access_key" {
  description = "AWS access key"
  type        = string
}

variable "aws_secret_key" {
  description = "AWS secret key"
  type        = string
}

variable "aws_session_token" {
  description = "AWS session token"
  type        = string
}

variable "ami_id" {
  description = "AMI ID for EC2 (Ubuntu 22.04 LTS)"
  type        = string
  default = "ami-020cba7c55df1f615"
}

variable "instance_type" {
  description = "EC2 instance type"
  default     = "t3.large"
}

variable "key_name" {
  description = "SSH key pair name"
  type        = string
  default = "vockey"
}
