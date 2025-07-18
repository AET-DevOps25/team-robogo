# Team RoboGo

A comprehensive robotics competition management system with multi-screen presentation capabilities, real-time score tracking, and AI-powered content generation.

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose (all other dependencies included in containers)

### Local Setup (3 Commands)

```bash
# 1. Clone and navigate
git clone https://github.com/your-org/team-robogo.git && cd team-robogo

# 2. Start all services
docker-compose up -d

# 3. Verify deployment
curl -s http://localhost:8081/actuator/health | jq '.status' || echo "Backend starting..."
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **GenAI Service**: http://localhost:8000/docs

## 🏗️ System Architecture

![System Architecture](documentation/architecture.png)

### Core Components

- **Frontend**: Vue.js 3 with Tailwind CSS
- **Backend**: Spring Boot 3 with JPA/Hibernate
- **Database**: PostgreSQL with Redis caching
- **AI Service**: FastAPI-based GenAI integration
- **Infrastructure**: Docker Compose with Kubernetes support

## 🤖 GenAI Integration

### AI-Powered Features

The system integrates GenAI services for enhanced content generation and management:

#### 1. **Slide Content Generation**
```python
# Example GenAI service usage
POST /genai/generate-slide-content
{
  "prompt": "Create a slide about robotics competition rules",
  "slide_type": "information",
  "style": "professional"
}
```

#### 2. **Score Analysis**
```python
# AI-powered score analysis
POST /genai/analyze-scores
{
  "team_scores": [...],
  "analysis_type": "trend_analysis"
}
```

#### 3. **Content Optimization**
```python
# Content optimization for different screen sizes
POST /genai/optimize-content
{
  "content": "slide_content",
  "target_screen": "large_display"
}
```

### GenAI Service Configuration

```yaml
# docker-compose.yaml - GenAI service
genai:
  image: team-robogo/genai:latest
  ports:
    - "8000:8000"
  environment:
    - OPENAI_API_KEY=${OPENAI_API_KEY}
    - MODEL_NAME=gpt-4
    - MAX_TOKENS=2000
  volumes:
    - ./genai/models:/app/models
```

### AI Model Management

```bash
# Update AI models
docker-compose exec genai python -m pip install -r requirements.txt

# Test AI service
curl -X POST http://localhost:8000/health
```

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

```yaml
# .github/workflows/ci-cd.yml
name: CI/CD Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up Java
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          
      - name: Set up Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          
      - name: Set up Python
        uses: actions/setup-python@v4
        with:
          python-version: '3.9'
          
      - name: Run Backend Tests
        run: |
          cd server
          ./gradlew test
          
      - name: Run Frontend Tests
        run: |
          cd client
          npm install
          npm run test
          
      - name: Run GenAI Tests
        run: |
          cd genai
          pip install -r requirements.txt
          python -m pytest

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build Docker images
        run: |
          docker build -t team-robogo/server:latest ./server
          docker build -t team-robogo/client:latest ./client
          docker build -t team-robogo/genai:latest ./genai
          
      - name: Push to Registry
        if: github.ref == 'refs/heads/main'
        run: |
          echo ${{ secrets.DOCKER_PASSWORD }} | docker login -u ${{ secrets.DOCKER_USERNAME }} --password-stdin
          docker push team-robogo/server:latest
          docker push team-robogo/client:latest
          docker push team-robogo/genai:latest

  deploy:
    needs: build
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to Production
        run: |
          # Kubernetes deployment
          kubectl apply -f infra/helm/screen/
```

### Automated Testing

#### Backend Testing
```bash
# Run all tests
cd server && ./gradlew test

# Run specific test
./gradlew test --tests "ScoreControllerTest"

# Integration tests
./gradlew integrationTest
```

#### Frontend Testing
```bash
# Unit tests
cd client && npm run test

# E2E tests
npm run test:e2e

# Coverage report
npm run test:coverage
```

#### GenAI Testing
```bash
# Python tests
cd genai && python -m pytest

# API tests
pytest tests/test_routes.py

# Model tests
pytest tests/test_services.py
```

### Quality Gates

```yaml
# Quality checks in CI/CD
quality-checks:
  - name: Code Coverage
    threshold: 80%
    
  - name: Security Scan
    tools: [snyk, sonarqube]
    
  - name: Performance Test
    threshold: 2s response time
    
  - name: AI Model Validation
    check: model_accuracy > 0.85
```

## 🚀 Deployment

### Development Environment

```bash
# Start with hot reload
docker-compose -f docker-compose.dev.yml up --build

# Stop all services
docker-compose down
```

### Production Environment

```bash
# Deploy to Kubernetes (requires kubectl access)
kubectl apply -f infra/helm/screen/

# Check deployment status
kubectl get pods -n robogo -w
```

### Platform-Specific Setup

#### macOS
```bash
# Install Docker Desktop
brew install --cask docker

# Start services
docker-compose up -d
```

#### Ubuntu/Debian
```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh && sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Start services
docker-compose up -d
```

#### Windows
```bash
# Install Docker Desktop from https://www.docker.com/products/docker-desktop

# Start services (PowerShell)
docker-compose up -d
```

### Environment Configuration

```bash
# Copy environment template
cp .env.example .env

# Set required variables
echo "OPENAI_API_KEY=your-api-key" >> .env
echo "JWT_SECRET=your-secret-key" >> .env
```

## 📊 Monitoring & Observability

### Application Metrics

```yaml
# Prometheus configuration
monitoring:
  - name: Application Metrics
    endpoint: /actuator/prometheus
    
  - name: AI Service Metrics
    endpoint: /metrics
    
  - name: Database Metrics
    endpoint: /metrics/postgres
```

### Logging

```yaml
# Centralized logging with ELK stack
logging:
  - application: team-robogo-server
    level: INFO
    
  - application: team-robogo-genai
    level: DEBUG
    
  - application: team-robogo-client
    level: WARN
```

## 🔧 Development

### Local Development Setup

```bash
# Start development environment with hot reload
docker-compose -f docker-compose.dev.yml up --build

# View logs
docker-compose logs -f server
```

### Database Operations

```bash
# Run migrations
docker-compose exec server ./gradlew flywayMigrate

# Reset database
docker-compose exec server ./gradlew flywayClean flywayMigrate
```

### API Documentation

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8081/v3/api-docs
- **GenAI API**: http://localhost:8000/docs

## 🧪 Testing

### Test Coverage

```bash
# Run all tests
docker-compose exec server ./gradlew test
docker-compose exec client npm test
docker-compose exec genai python -m pytest

# Generate coverage reports
docker-compose exec server ./gradlew jacocoTestReport
```

### Performance Testing

```bash
# Load testing (requires k6)
k6 run tests/load-test.js

# API health check
curl -f http://localhost:8081/actuator/health || exit 1
```

## 📚 Documentation

- [System Architecture](documentation/top-level-architecture.md)
- [API Documentation](documentation/api-endpoints.md)
- [Data Schema](documentation/data-schema.md)
- [Use Case Analysis](documentation/use-case-diagram.md)
- [Requirements Specification](documentation/requirements.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow the existing code style
- Add tests for new features
- Update documentation as needed
- Ensure CI/CD pipeline passes

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Issues**: [GitHub Issues](https://github.com/your-org/team-robogo/issues)
- **Documentation**: [Project Wiki](https://github.com/your-org/team-robogo/wiki)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/team-robogo/discussions)

---

**Team RoboGo** - Empowering robotics competitions with intelligent presentation management.
