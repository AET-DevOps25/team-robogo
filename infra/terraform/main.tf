data "aws_vpc" "default" {
  default = true
}

resource "random_id" "suffix" {
  byte_length = 4
}

provider "aws" {
  region     = var.aws_region
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
  token = var.aws_session_token
}

resource "aws_security_group" "robogo_ec2_sg" {
  name        = "robogo-ec2-sg-${random_id.suffix.hex}"
  description = "Allow SSH, Kubernetes, and HTTP/HTTPS traffic"
  vpc_id      = data.aws_vpc.default.id

  # SSH
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Kubernetes API
  ingress {
    from_port   = 6443
    to_port     = 6443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP
  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "robogo_ec2" {
  ami           = var.ami_id
  instance_type = var.instance_type
  key_name      = var.key_name
  vpc_security_group_ids = [aws_security_group.robogo_ec2_sg.id]

  root_block_device {
    volume_size = 50
    volume_type = "gp3"
  }

  tags = {
    Name = "team-robogo-ec2-${random_id.suffix.hex}"
  }
}
