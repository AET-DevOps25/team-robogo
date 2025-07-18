# Team RoboGo Server

This is the backend service for the Team RoboGo project, providing REST APIs for slide deck management, score tracking, and multi-screen synchronization.

## Features

- **Slide Deck Management**: Create, update, and synchronize slide decks
- **Score Management**: Track team scores and rankings
- **Screen Management**: Control display screens and their content
- **Real-time Synchronization**: Multi-screen synchronization with version control
- **Authentication**: JWT-based authentication system
- **API Documentation**: Integrated Swagger UI for interactive API testing

## Quick Start

### Prerequisites
- Java 17 or higher
- Gradle 7.0 or higher
- Docker (for Docker Compose environment)

### Development Environment

1. **Start the application:**
   ```bash
   ./gradlew bootRun
   ```

2. **Access Swagger UI:**
   - Open your browser and visit: http://localhost:8081/swagger-ui.html
   - Or access the OpenAPI JSON: http://localhost:8081/v3/api-docs

3. **Test the APIs:**
   - All REST API endpoints are automatically documented
   - Use the interactive interface to test endpoints
   - Authentication is required for most endpoints

### Docker Compose Environment

1. **Start the services:**
   ```bash
   # From project root directory
   docker-compose up -d
   ```

2. **Access Swagger UI:**
   - Same URL as development: http://localhost:8081/swagger-ui.html
   - Services are exposed on the host machine

## API Documentation

### Authentication
Most APIs require authentication. To test protected endpoints:

1. **Login to get JWT token:**
   ```bash
   POST /auth/login
   {
     "username": "admin",
     "password": "admin"
   }
   ```

2. **Set authentication in Swagger UI:**
   - Click the "Authorize" button in the top right
   - Enter: `Bearer <your-jwt-token>`
   - Click "Authorize"

### API Categories

- **Authentication Management**: User login, session validation
- **Slide Deck Management**: Slide creation, updates, synchronization
- **Score Management**: Team score records and rankings
- **Screen Management**: Display screen control and status
- **Category Management**: Team category management
- **Team Management**: Participating team information
- **Image Management**: Image upload and content management

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# Server port
server.port=8081

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/robogo
spring.datasource.username=postgres
spring.datasource.password=password

# JWT configuration
jwt.secret=your-secret-key
jwt.expiration=86400000

# Swagger UI configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

### Environment Variables
For Docker Compose, environment variables can be set in `docker-compose.yaml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=prod
  - SERVER_PORT=8081
  - DB_HOST=postgres
  - DB_PORT=5432
```

## Development

### Project Structure
```
src/main/java/de/fll/screen/
├── controller/     # REST API controllers
├── service/        # Business logic services
├── repository/     # Data access layer
├── model/          # Entity classes
├── dto/            # Data transfer objects
├── config/         # Configuration classes
└── assembler/      # DTO-Entity converters
```

### Key Dependencies
```groovy
// Spring Boot
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-security'

// Database
implementation 'org.postgresql:postgresql'
implementation 'org.flywaydb:flyway-core'

// JWT
implementation 'io.jsonwebtoken:jjwt-api'

// API Documentation
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'

// Testing
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.testcontainers:postgresql'
```

### Building and Testing
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=test'
```

## Troubleshooting

### Common Issues

**Q: Cannot access Swagger UI**
- **Development**: Check if application is running on port 8081
- **Docker Compose**: Check container logs with `docker-compose logs server`

**Q: Database connection fails**
- Ensure PostgreSQL is running
- Check database credentials in application.properties
- For Docker Compose, ensure postgres service is healthy

**Q: Authentication fails**
- Verify JWT token format: `Bearer <token>`
- Check token expiration
- Ensure username/password are correct

**Q: File upload fails**
- Check file size limit (default: 100MB)
- Verify file format is supported
- Check disk space availability

## Related Documentation

- [API Endpoints Documentation](../documentation/api-endpoints.md)
- [Data Schema Documentation](../documentation/data-schema.md)
- [Swagger UI User Guide](../documentation/swagger-ui-guide.md)
- [Project Documentation Index](../documentation/README.md)

## Contributing

1. Follow the existing code structure
2. Add appropriate Swagger annotations to new endpoints
3. Include tests for new functionality
4. Update documentation as needed

## License

This project is part of the Team RoboGo system. See the main [LICENSE](../LICENSE) file for details. 