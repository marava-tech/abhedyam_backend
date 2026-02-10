# Review APIs

These endpoints exist in the backend but are NOT listed in `used_apis.md` (no client is known to call them).
They were **not removed** in the cleanup pass because they may serve infrastructure, background jobs, or undocumented workflows.

**Please review each group and confirm: remove / keep / migrate.**

---

## Infrastructure (strongly recommend keeping)

| Endpoint | Method | Controller | Reason |
|----------|--------|------------|--------|
| `/api/v1/webhook/razorpay` | POST | SubscriptionController | Razorpay calls this directly — not a client call |
| `/api/v1/health` | GET | HealthController | Used by load balancers / uptime monitoring |
| `/api/v1/cache/invalidate` | POST | CacheController | Infrastructure cache management |

---

## UserController (all 4 endpoints)

`User` is the base entity for `Owner` and `Customer`. These endpoints may be used for internal identity management.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/users` | POST | Create base user |
| `/api/v1/users/{id}` | GET | Fetch user by ID |
| `/api/v1/users` | GET | List all users |
| `/api/v1/users/{id}` | PATCH | Update user |

---

## OwnerSettingsController (2 endpoints)

May have been superseded by owner profile update (`PATCH /owners/{id}`), but could still be active.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/owners/{ownerId}/settings` | GET | Get owner-specific settings |
| `/api/v1/owners/{ownerId}/settings` | PATCH | Update owner settings |

---

## InventoryController (entire controller, 5 endpoints)

All 5 endpoints are marked `permitAll` in SecurityConfig (public, no auth required). Unusual — may be intentional for supply chain access or may be a leftover.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/inventories` | POST | Create inventory |
| `/api/v1/inventories/{id}` | GET | Get inventory by ID |
| `/api/v1/inventories` | GET | List all inventories |
| `/api/v1/inventories/owner/{ownerId}` | GET | Get inventories by owner |
| `/api/v1/inventories/{id}` | PUT | Update inventory |

---

## Stock mutation endpoints (3 endpoints)

`PUT /stock/update` is used by the owner app. These 3 are not — but may be called as part of supply/warehouse workflows.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/stock/purchase-in` | POST | Record stock purchase/inward |
| `/api/v1/stock/sale-out` | POST | Deduct stock on sale |
| `/api/v1/stock/adjust` | POST | Manual stock adjustment |

---

## AuditController (entire controller, 5 endpoints)

Audit trail management. May be used for compliance, but no client accesses it directly.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/audits` | POST | Create audit entry |
| `/api/v1/audits/{id}` | GET | Get audit by ID |
| `/api/v1/audits` | GET | List all audits |
| `/api/v1/audits/owner/{ownerId}` | GET | Get audits for an owner |
| `/api/v1/audits/{id}` | PUT | Update audit |

---

## Sale / transaction endpoints

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/sales/transaction/{transactionId}` | GET | Get transaction details (could power receipt view) |
| `/api/v1/sales/transaction/{transactionId}/cancel` | POST | Cancel a sale |
| `/api/v1/sale-items/{id}` | GET | Fetch individual sale item |

---

## Payment endpoints

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/payments/{id}` | GET | Fetch individual payment |
| `/api/v1/payments/upi-link` | POST | Generate UPI payment link |
| `/api/v1/user/subscription` | GET | Get subscription status for current user |
| `/api/v1/upi-accounts/{id}/primary` | PUT | Set a UPI account as primary |

---

## Product lookup

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/products/{id}` | GET | Fetch product by ID |

---

## Reminder / notification background endpoints

May be used by scheduled background jobs, not direct client calls.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/reminders/pending` | GET | Get pending reminders (background job trigger) |
| `/api/v1/reminders/{id}/mark-sent` | PATCH | Mark reminder as sent (notification dispatch) |

---

## Document deletion

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/documents/{id}` | DELETE | Delete a document — natural part of document management |

---

## Stats maintenance

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/stats/recompute` | POST | Admin tool to recompute statistics |

---

## Admin DailyQuote management

`GET /daily-quotes/today` is used by clients. Someone needs to manage the quotes — likely via these admin endpoints.

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/daily-quotes` | POST | Create a new daily quote |
| `/api/v1/daily-quotes/{id}` | PUT | Update an existing daily quote |

---

## Summary

| Category | Endpoint Count | Recommendation |
|----------|---------------|----------------|
| Infrastructure (webhook, health, cache) | 3 | **Keep** |
| UserController | 4 | Review — likely safe to remove if User CRUD is via Owner/Customer |
| OwnerSettingsController | 2 | Review — check if replaced by owner PATCH |
| InventoryController | 5 | Review — check why endpoints are public |
| Stock mutations | 3 | Review — check if any background job calls these |
| AuditController | 5 | Review — check if auto-auditing makes REST CRUD redundant |
| Sale / transaction | 3 | Review |
| Payment misc | 4 | Review |
| Product by ID | 1 | Review — likely safe to remove |
| Reminder background | 2 | Review — check scheduler code |
| Document delete | 1 | Likely **keep** |
| Stats recompute | 1 | Review |
| DailyQuote admin | 2 | Likely **keep** (needed to manage quotes) |
| **Total** | **36** | |
