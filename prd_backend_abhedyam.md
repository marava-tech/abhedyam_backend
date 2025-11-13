## Abhedyam Backend PRD (Detailed for Cursor)

### 1. Overview
Abhedyam backend powers a mobile-first business management app for small merchants and shop owners in villages. It manages user onboarding (via OTP), inventory, customers, sales, analytics, payments, and notifications.

The backend is written in **Java Spring Boot** and exposes a well-documented REST API consumed by a Flutter frontend.

### 2. Goal
Deliver a stable, secure, and modular backend that supports all finalized Abhedyam functionalities with clear contracts (OpenAPI), clean architecture, and full test coverage.

### 3. Target Users
- Village business owners (end users)
- App itself (frontend)
- System admin (for debugging / monitoring)

### 4. Core Functionalities
1. **User Management**: Register/login via mobile + OTP (Fast2SMS). JWT-based session tokens.  
2. **Customer Management**: CRUD operations for customers, linked to the owner.
3. **Product Management**: CRUD for products. AI-generated product images.
4. **Inventory Management**: Stock ledger (append-only `stock_entries`) with item count tracking.
5. **Sales Recording**: Record sales, auto-update stock, attach location (lat/long).
6. **Payments (UPI P2M)**: Generate UPI links via Cashfree; webhook confirmation; idempotent requests.
7. **Notifications/Reminders**: Schedule and send reminders (Fast2SMS integration). Queue-based async jobs.
8. **AI Services**: OCR-based record creation (upload notebook images → AI API → structured data).
9. **Analytics & Stats**: Aggregate daily/weekly/monthly metrics; provide insights & quotes.
10. **Daily Quote Generator**: Use ChatGPT API to fetch and store daily quote + business tip.
11. **Call Logs (optional)**: Track who called whom (owner ↔ customer).
12. **Notes & Reminders**: Per-customer notes and reminders (notification triggers supported).

### 5. Non-Functional Requirements
- Modular Monolith architecture (Spring Boot multi-module).
- OpenAPI v3 specification + Swagger UI (must).
- Structured JSON logging with correlation & request IDs.
- Micrometer + Prometheus metrics.
- Caching (Redis) for heavy reads.
- Pagination for all list endpoints.
- Config-driven feature toggles.
- Strict input validation + sanitation.
- Rate-limiting for OTP and payment APIs.
- Unit tests for all business logic.
- Audit immutability for financial actions.
- JSON envelopes for responses & machine-readable error codes.
- Dockerized for local + production.
- CI: build, test, OpenAPI validation, lint.

### 6. Integrations
| Integration | Purpose | Type |
|--------------|----------|------|
| Fast2SMS | OTPs, reminders | External REST |
| Cashfree | Payment (UPI P2M) | External REST |
| ChatGPT API | Business quotes/tactics | External REST |
| Google Maps | Location, route optimization | External REST |
| Redis | Cache + async queue | Internal |
| MySQL | Primary DB | Internal |
| MongoDB | Optional (AI blobs) | Internal |

### 7. API Contract Rules
- Every endpoint versioned: `/api/v1/...`
- Response format:
  ```json
  { "success": true, "data": { ... } }
  { "success": false, "code": "ERR_INVALID_OTP", "message": "Invalid OTP" }
  ```
- OpenAPI spec stored in `/docs/openapi.yaml`
- Swagger UI auto-hosted under `/swagger-ui`.

### 8. Modules
- **auth** → OTP flow, JWT auth, Fast2SMS integration.
- **users** → user profiles, roles, and merchant KYC.
- **customers** → CRUD + notes/reminders.
- **products** → CRUD + AI image generation.
- **inventory** → stock entries ledger.
- **sales** → create, update, payment tracking.
- **payments** → Cashfree integration, UPI link generation, webhook handling.
- **notifications** → SMS reminders, internal scheduling.
- **ai** → OCR image upload + parse via AI API.
- **analytics** → stats + daily quotes.
- **core/common** → utilities, shared DTOs, error envelopes.

### 9. Data Storage
- **MySQL** — transactional data (users, customers, products, sales, stock, payments, reminders).
- **Redis** — cache + OTP + rate limits + async jobs.
- **MongoDB (optional)** — raw AI/OCR results or image metadata.

### 10. Async / Queue Jobs
- Notification send jobs.
- AI image parse jobs.
- Payment webhook processing.

### 11. Testing
- Unit tests for all business logic.
- Integration tests for auth, payment, sales.
- Contract tests verifying OpenAPI compliance.

### 12. Observability
- Prometheus metrics (Micrometer).
- Grafana dashboard for: API latency, OTP failure rate, payment conversion.
- Sentry for exceptions.

### 13. Environment Variables
- `FAST2SMS_API_KEY`
- `CASHFREE_CLIENT_ID`
- `CASHFREE_SECRET`
- `CHATGPT_API_KEY`
- `DB_*` (MySQL)
- `REDIS_*`

### 14. Future Enhancements
- WhatsApp reminders.
- Marketplace model for multiple store owners.
- AI-based business insights.
- Export analytics as Excel/PDF.

---
This PRD acts as the development reference for Cursor and backend engineers.

