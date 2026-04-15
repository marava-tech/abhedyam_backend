# Architecture — Abhedyam Backend

## System overview

Three client apps → Spring Boot REST API → MongoDB (primary) + MySQL (secondary) + Redis (cache)

Clients:
- Abhedyam Flutter app (owner-facing) — Firebase JWT auth
- Connect Flutter app (customer-facing) — phone OTP auth
- Dashboard React/Vite (admin) — email/password auth

## Authentication
- Owner app: Firebase Google Sign-in → Firebase token → backend exchanges for JWT
- Customer app: Phone OTP via 2Factor API → backend issues JWT
- Admin dashboard: Email/password → backend issues JWT
- All JWTs validated on every request via Spring Security filter

## Package structure pattern
```
controller/ ← receives HTTP, validates input, delegates to service, returns DTO
service/    ← business logic, interface (IXxxService) + impl (XxxServiceImpl)
repository/ ← data access only, no business logic
model/      ← DB entity classes
dto/        ← request/response shapes (never pass entities through controller boundary)
exception/  ← domain exceptions (ResourceNotFoundException, etc.)
config/     ← Spring beans (SecurityConfig, CorsConfig, CloudinaryConfig, etc.)
```

## Database design decisions
- **MongoDB as primary**: Customers, products, sales, payments, subscriptions — document model fits the flexible khata (ledger) data structure
- **MySQL as secondary**: Analytics aggregations, audit logs — relational structure for reporting queries
- **Redis as cache only**: OTP cache, session cache, rate limiting. Never store business data here.
- MongoDB indexes: Always add `@Indexed` on individual fields queried alone, even when compound indexes exist

## Key design patterns
- **Interface + impl for services**: Every service has `IXxxService` interface and `XxxServiceImpl`. Enables easy mocking in tests.
- **`PageResponse<T>` wrapper**: All paginated endpoints return this with `totalElements`, `totalPages`, `hasNext`, `hasPrevious`
- **`ApiResponse<T>` envelope**: All responses wrapped for consistent structure
- **Idempotency for payment/OTP**: Payment verify and OTP send endpoints designed to be safe on retry

## Subscriptions
Three tiers: GO (free), PRO, PLUS. Payments via Razorpay. Subscription state checked on feature access.

## File uploads
Files go to MinIO storage via the upload-api service (https://s3.marava.tech/upload). Abhedyam backend doesn't handle file storage directly.
