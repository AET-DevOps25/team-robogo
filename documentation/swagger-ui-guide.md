# Swagger UI User Guide

## Overview

The Team RoboGo project integrates Swagger UI, providing complete API documentation and interactive testing interface.

## Access Methods

### Development Environment
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

### Docker Compose Environment
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

**Note**: The Docker Compose environment uses the same localhost URLs as development, since the services are exposed on the host machine.

## Features

### 1. Interactive API Documentation
- All API endpoints have detailed descriptions and parameter explanations
- Support direct API testing in browser
- Automatically generates request and response examples

### 2. Authentication Support
- Integrated JWT authentication mechanism
- Can directly input Bearer Token in Swagger UI for testing
- Supports "Authorize" button for global authentication

### 3. Category Management
APIs are categorized by functional modules:
- **Authentication Management**: User login, session validation, etc.
- **Slide Deck Management**: Slide creation, updates, synchronization, etc.
- **Score Management**: Team score records and rankings
- **Screen Management**: Display screen control and status management
- **Category Management**: Team category management
- **Team Management**: Participating team information management
- **Image Management**: Image upload and content management

## Usage Steps

### 1. Start Application

**Development Environment:**
```bash
# Run in server directory
./gradlew bootRun
```

**Docker Compose Environment:**
```bash
# Run from project root directory
docker-compose up -d
```

### 2. Access Swagger UI

**Development Environment:**
Open browser and visit: http://localhost:8081/swagger-ui.html

**Docker Compose Environment:**
Open browser and visit: http://localhost:8081/swagger-ui.html

**Note**: Both environments use the same URL since Docker Compose exposes the server port to the host machine.

### 3. Authentication
1. Click the "Authorize" button in the top right corner
2. Enter JWT token in the popup window (format: Bearer <your-token>)
3. Click "Authorize" to confirm

### 4. Test APIs
1. Select the API endpoint to test
2. Click "Try it out" button
3. Fill in necessary parameters
4. Click "Execute" to run the request
5. View response results

## Authentication Flow Example

### 1. User Login
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

### 2. Get JWT Token
Copy the token field value from the login response

### 3. Set Authentication in Swagger UI
1. Click "Authorize" button
2. Enter: `Bearer <your-jwt-token>`
3. Click "Authorize"

### 4. Test Protected APIs
Now you can test all APIs that require authentication

## API Group Descriptions

### Authentication Management
- `POST /auth/login` - User login
- `GET /auth/session` - Validate session
- `POST /auth/logout` - User logout

### Slide Deck Management
- `GET /slidedecks` - Get all slide decks
- `POST /slidedecks` - Create slide deck
- `PUT /slidedecks/{id}` - Update slide deck
- `DELETE /slidedecks/{id}` - Delete slide deck
- `POST /slidedecks/{id}/slides` - Add slide
- `POST /slidedecks/{id}/sync` - Synchronization status

### Score Management
- `GET /scores/category/{id}` - Get category scores
- `POST /scores` - Add score
- `PUT /scores/{id}` - Update score
- `DELETE /scores/{id}` - Delete score

### Screen Management
- `GET /screens` - Get all screens
- `POST /screens` - Create screen
- `PUT /screens/{id}` - Update screen
- `POST /screens/{id}/assign-slide-deck/{deckId}` - Assign slide deck

## Common Issues

### Q: Cannot access Swagger UI
**Development Environment**: Check if the application is running properly, confirm port 8081 is not occupied

**Docker Compose Environment**: 
- Check if Docker Compose containers are running: `docker-compose ps`
- Check container logs: `docker-compose logs server`
- Ensure port 8081 is not occupied by other services

### Q: API calls return 401 error
A: Ensure JWT token is set correctly, token format should be: `Bearer <token>`

### Q: File upload fails
A: Check if file size exceeds 100MB limit, ensure file format is correct

### Q: Synchronization doesn't work
A: Check if version numbers are correct, ensure client and server time are synchronized

## Development Recommendations

### 1. Use Swagger UI for API Testing
- Use Swagger UI to test APIs during development
- Can quickly verify API correctness
- Supports API documentation export

### 2. Keep Documentation Updated
- Update Swagger annotations when modifying APIs
- Ensure documentation is consistent with actual APIs
- Add detailed parameter explanations and examples

### 3. Error Handling
- All APIs should have appropriate error responses
- Use standard HTTP status codes
- Provide meaningful error messages

## Extension Features

### 1. Custom Themes
Can customize Swagger UI appearance by modifying CSS

### 2. Multi-Environment Support
Can configure different server URLs for different environments

**Current Environment Setup:**
- Development: Direct Gradle execution
- Docker Compose: Containerized services with host port exposure
- Kubernetes: Not currently exposed (Swagger UI not accessible in K8s environment)

### 3. Security Configuration
Can configure different security schemes such as OAuth2, API Key, etc.

## Related Links

- [SpringDoc OpenAPI Official Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Configuration Options](https://swagger.io/docs/open-source-tools/swagger-ui/usage/configuration/) 