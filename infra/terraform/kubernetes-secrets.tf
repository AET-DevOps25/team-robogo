# Kubernetes Provider Configuration
provider "kubernetes" {
  config_path = "~/Downloads/student.yaml"
}

# GenAI Secret
resource "kubernetes_secret" "genai_secret" {
  metadata {
    name      = "genai-secret"
    namespace = "devops25-team-robogo"
  }

  type = "Opaque"

  data = {
    CHAIR_API_KEY  = var.chair_api_key
    OPENAI_API_KEY = var.openai_api_key
  }
} 