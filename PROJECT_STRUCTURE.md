# Abhedyam Backend - Project Structure

## Overview
Clean Spring Boot 3.2.1 project with Java 21, following best practices and modular architecture.

## Package Structure

```
com.abhedyam/
├── AbhedyamBackendApplication.java    # Main application entry point
│
├── config/                            # Configuration classes
│   ├── JpaConfig.java                # JPA auditing configuration
│   ├── OpenApiConfig.java            # Swagger/OpenAPI configuration
│   └── SecurityConfig.java           # Spring Security configuration
│
├── controller/                        # REST API controllers
│   └── HealthController.java         # Health check endpoint
│
├── dto/                              # Data Transfer Objects
│   └── ApiResponse.java              # Standard API response wrapper
│
├── exception/                        # Exception handling
│   ├── BusinessException.java        # Custom business exceptions
│   ├── GlobalExceptionHandler.java   # Global exception handler
│   └── ResourceNotFoundException.java # Resource not found exception
│
├── model/                            # JPA entity models
│   └── BaseEntity.java              # Base entity with audit fields
│
├── repository/                       # Data access layer (JPA repositories)
│   └── [Empty - ready for repositories]
│
├── service/                          # Business logic layer
│   └── [Empty - ready for services]
│
└── util/                            # Utility classes
    └── [Empty - ready for utilities]
```

## Key Features Configured

### 1. Dependencies
- Spring Boot Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Data Redis
- Spring Actuator
- Micrometer Prometheus
- MySQL Connector
- Lombok
- JWT (jjwt)
- SpringDoc OpenAPI

### 2. Configuration
- **Database**: MySQL with JPA/Hibernate
- **Cache**: Redis support
- **Security**: JWT-based stateless authentication
- **API Docs**: OpenAPI 3.0 with Swagger UI
- **Metrics**: Prometheus endpoint enabled
- **Context Path**: `/api/v1`

### 3. API Response Format
All endpoints return standardized responses:

**Success:**
```json
{
  "success": true,
  "data": { ... }
}
```

**Error:**
```json
{
  "success": false,
  "code": "ERR_CODE",
  "message": "Error description"
}
```

### 4. Base Entity
All entities can extend `BaseEntity` which provides:
- `id` (auto-generated)
- `createdAt` (auto-populated)
- `updatedAt` (auto-updated)

### 5. Exception Handling
Global exception handler covers:
- `ResourceNotFoundException` → 404
- `BusinessException` → 400 with custom error code
- `MethodArgumentNotValidException` → 400 with validation errors
- Generic exceptions → 500

### 6. Security
- Stateless JWT authentication
- Public endpoints: `/api/v1/auth/**`, `/swagger-ui/**`, `/api-docs/**`, `/actuator/**`
- All other endpoints require authentication

## Endpoints

### Health Check
- **GET** `/api/v1/health` - Returns service status

### Swagger UI
- **URL**: `http://localhost:8080/api/v1/swagger-ui/index.html`

### API Docs (JSON)
- **URL**: `http://localhost:8080/api/v1/api-docs`

### Actuator
- **URL**: `http://localhost:8080/api/v1/actuator`
- Endpoints: health, info, prometheus, metrics

## Environment Configuration

See `.env.example` for all required environment variables:
- Database credentials
- Redis connection
- JWT secret
- External API keys (Fast2SMS, Cashfree, ChatGPT)

## Build & Run

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn clean package

# Run application
mvn spring-boot:run

# Docker build
docker build -t abhedyam-backend .
```

## Next Steps

Ready to implement domain-specific features:
1. User Management (auth module)
2. Customer Management
3. Product Management
4. Inventory Management
5. Sales Recording
6. Payment Integration
7. Notifications
8. AI Services
9. Analytics

Each module will add:
- Entity models in `model/`
- DTOs in `dto/`
- Repositories in `repository/`
- Services in `service/`
- Controllers in `controller/`

