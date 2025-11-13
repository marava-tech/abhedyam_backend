# Abhedyam Backend

Backend service for Abhedyam Business Management Application built with Spring Boot 3.x and Java 21.

## Tech Stack

- Java 21
- Spring Boot 3.2.1
- Spring Data JPA
- Spring Security
- MySQL
- Redis
- OpenAPI 3.0 (Swagger)
- Prometheus Metrics

## Project Structure

```
src/main/java/com/abhedyam/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── exception/      # Exception handlers
├── model/          # Entity models
├── repository/     # Data access layer
├── service/        # Business logic
└── util/           # Utility classes
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+

### Environment Variables

```
DB_URL=jdbc:mysql://localhost:3306/abhedyam
DB_USERNAME=root
DB_PASSWORD=root
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-secret-key
FAST2SMS_API_KEY=your-api-key
CASHFREE_CLIENT_ID=your-client-id
CASHFREE_SECRET=your-secret
CHATGPT_API_KEY=your-api-key
```

### Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

### API Documentation

Once the application is running, access Swagger UI at:
```
http://localhost:8080/api/v1/swagger-ui/index.html
```

## API Endpoints

All endpoints are versioned under `/api/v1/`

### Health Check
- `GET /api/v1/health` - Service health status

# marava_abhedyam_backend
