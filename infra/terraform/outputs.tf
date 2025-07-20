output "ec2_public_ip" {
  description = "Public IP of the EC2 instance"
  value       = aws_instance.robogo_ec2.public_ip
}

output "ec2_public_dns" {
  value = aws_instance.robogo_ec2.public_dns
}
