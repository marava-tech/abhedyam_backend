# Abhedyam Backend - Quick Start Guide

## ✅ Project Created Successfully!

A clean Spring Boot 3.2.1 project with Java 21 has been set up with all necessary packages and configurations.

## 📦 What's Included

### Core Dependencies
- ✅ Spring Boot 3.2.1
- ✅ Java 21
- ✅ Spring Data JPA (MySQL)
- ✅ Spring Security (JWT ready)
- ✅ Spring Data Redis
- ✅ Spring Validation
- ✅ Spring Actuator (Prometheus metrics)
- ✅ OpenAPI 3.0 / Swagger UI
- ✅ Lombok
- ✅ JWT (jjwt 0.12.3)

### Package Structure
```
com.abhedyam/
├── config/          ✅ JPA, Security, OpenAPI configs
├── controller/      ✅ Health check endpoint
├── dto/            ✅ ApiResponse wrapper
├── exception/      ✅ Global exception handler
├── model/          ✅ BaseEntity with audit fields
├── repository/     ✅ Ready for JPA repositories
├── service/        ✅ Ready for business logic
└── util/           ✅ Ready for utilities
```

## 🚀 How to Run

### Prerequisites
```bash
# Check Java version
java -version  # Should be 21+

# Check Maven
mvn -version   # Should be 3.8+
```

### Option 1: Run with Maven (No DB required for testing)
```bash
cd /home/madhu/Desktop/abhedyam/abhedyam_backend

# Compile
mvn clean compile

# Run with profile (will need DB)
mvn spring-boot:run
```

### Option 2: Run Tests (Uses H2 in-memory DB)
```bash
mvn test
```

### Option 3: Package and Run JAR
```bash
mvn clean package -DskipTests
java -jar target/abhedyam-backend-0.0.1-SNAPSHOT.jar
```

## 🗄️ Database Setup (Required for running the app)

### MySQL Setup
```sql
CREATE DATABASE abhedyam;
CREATE USER 'abhedyam_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON abhedyam.* TO 'abhedyam_user'@'localhost';
FLUSH PRIVILEGES;
```

### Environment Variables
Create a `.env` file or export these:
```bash
export DB_URL="jdbc:mysql://localhost:3306/abhedyam"
export DB_USERNAME="abhedyam_user"
export DB_PASSWORD="your_password"
export REDIS_HOST="localhost"
export REDIS_PORT="6379"
export JWT_SECRET="your-very-long-secret-key-here"
```

## 📝 API Documentation

Once running, access:
- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui/index.html
- **API Docs (JSON)**: http://localhost:8080/api/v1/api-docs
- **Health Check**: http://localhost:8080/api/v1/health
- **Actuator**: http://localhost:8080/api/v1/actuator

## 🏗️ Project Features

### 1. Standard API Response Format
All endpoints return:
```json
{
  "success": true,
  "data": { ... }
}
```

Or for errors:
```json
{
  "success": false,
  "code": "ERR_CODE",
  "message": "Error description"
}
```

### 2. Global Exception Handling
- `ResourceNotFoundException` → 404
- `BusinessException` → 400 with error code
- Validation errors → 400 with field details
- Generic exceptions → 500

### 3. Security Configuration
- Stateless JWT authentication (ready to implement)
- Public endpoints: `/api/v1/auth/**`, `/swagger-ui/**`, `/api-docs/**`, `/actuator/**`
- All other endpoints require authentication

### 4. JPA Auditing
All entities can extend `BaseEntity` for automatic:
- `id` (auto-generated)
- `createdAt` (auto-populated)
- `updatedAt` (auto-updated)

### 5. Observability
- Prometheus metrics at `/api/v1/actuator/prometheus`
- Health checks at `/api/v1/actuator/health`

## 📂 File Structure
```
abhedyam_backend/
├── pom.xml                          # Maven dependencies
├── Dockerfile                       # Docker configuration
├── README.md                        # General documentation
├── PROJECT_STRUCTURE.md             # Detailed structure guide
├── QUICKSTART.md                    # This file
├── .gitignore                       # Git ignore rules
├── .cursorrules                     # Development rules
└── src/
    ├── main/
    │   ├── java/com/abhedyam/      # Application code
    │   └── resources/
    │       └── application.yml      # Configuration
    └── test/
        ├── java/com/abhedyam/      # Test code
        └── resources/
            └── application-test.yml # Test configuration
```

## ✨ Next Steps

Your project is ready! You can now:

1. **Start building features**:
   - Create entities in `model/`
   - Create DTOs in `dto/`
   - Create repositories in `repository/`
   - Create services in `service/`
   - Create controllers in `controller/`

2. **Example: Create a User entity**:
   ```java
   // model/User.java
   @Entity
   public class User extends BaseEntity {
       private String name;
       private String mobile;
   }
   
   // repository/UserRepository.java
   public interface UserRepository extends JpaRepository<User, Long> {}
   
   // service/UserService.java
   @Service
   public class UserService {
       // business logic
   }
   
   // controller/UserController.java
   @RestController
   @RequestMapping("/users")
   public class UserController {
       // endpoints
   }
   ```

3. **Everything compiles successfully** ✅
4. **Ready for feature development** ✅

## 🎯 Development Notes

- All API routes are prefixed with `/api/v1/`
- Use `ApiResponse.success(data)` for successful responses
- Use `ApiResponse.error(code, message)` for errors
- Tests use H2 in-memory database automatically
- Production uses MySQL + Redis
- JWT authentication framework is ready (implementation pending)

---

**Happy Coding! 🚀**

