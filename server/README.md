# OpenAPI (Swagger) API Documentation

This service integrates [springdoc-openapi-starter-webmvc-ui](https://springdoc.org/), which automatically generates Swagger-style API documentation and an online testing page after startup.

## How to use

1. Make sure the dependency is added in `build.gradle` (already configured):
   ```groovy
   implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0'
   ```
2. Start the Spring Boot service.
3. Open your browser and visit:
   - http://localhost:8080/swagger-ui.html
   - or http://localhost:8080/swagger-ui/index.html

You will see and be able to test all automatically generated REST API endpoints.

No manual documentation is needed; all Controller endpoints are scanned and displayed automatically. 