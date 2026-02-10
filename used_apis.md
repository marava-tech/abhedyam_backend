# Used APIs — Combined Reference

Aggregated list of all API endpoints actively used across the three clients: **Abhedyam** (owner app), **Connect** (customer app), and **Dashboard** (admin panel).
Use this as the baseline for API cleaning — any endpoint not listed here is a candidate for removal.

---

## 1. Abhedyam (Owner App)
*65 endpoints*

### Authentication & Onboarding
| Endpoint | Method |
|----------|--------|
| `/api/v1/auth/google/login` | POST |
| `https://s3.marava.tech/upload` | POST |
| `/api/v1/owner-onboarding` | POST |
| `/api/v1/owner-onboarding/owner/{ownerId}` | GET |

### Products & Inventory
| Endpoint | Method |
|----------|--------|
| `/api/v1/owners/{ownerId}/products` | GET |
| `/api/v1/products` | POST |
| `/api/v1/stock/{productId}/current` | GET |
| `/api/v1/stock/update` | PUT |
| `/api/v1/image-store/search` | GET |

### Customers & CRM
| Endpoint | Method |
|----------|--------|
| `/api/v1/owners/{ownerId}/customers` | GET |
| `/api/v1/customers/{customerId}` | GET |
| `/api/v1/customers` | POST |
| `/api/v1/owners/{ownerId}/customers/{customerId}/summary` | GET |
| `/api/v1/owners/{ownerId}/customers/{customerId}/sales-summary` | GET |
| `/api/v1/owners/{ownerId}/customers/{customerId}/payments-summary` | GET |
| `/api/v1/owners/{ownerId}/customers/{customerId}/notes-summary` | GET |
| `/api/v1/owners/{ownerId}/customers/{customerId}/reminders-summary` | GET |
| `/api/v1/location-details/customers/{customerId}` | GET |
| `/api/v1/notes/customer/{customerId}` | GET |
| `/api/v1/reminders/customer/{customerId}` | GET |
| `/api/v1/notes` | POST |
| `/api/v1/reminders` | POST |
| `/api/v1/owners/{ownerId}/customers/{customerId}/sale-items` | GET |
| `/api/v1/location-details/customers/{customerId}` | PATCH |
| `/api/v1/location-details/customers/locations` | POST |
| `/api/v1/location-details/villages` | GET |
| `/api/v1/customers/nearest` | POST |

### Sales & Payments
| Endpoint | Method |
|----------|--------|
| `/api/v1/sales` | POST |
| `/api/v1/owners/{ownerId}/payments` | GET |
| `/api/v1/payments/customer/{customerId}` | GET |
| `/api/v1/payments` | POST |
| `/api/v1/payments/{paymentId}/status` | PATCH |

### Stats & Dashboard
| Endpoint | Method |
|----------|--------|
| `/api/v1/stats` | GET |
| `/api/v1/daily-quotes/today` | GET |
| `/api/v1/stats/recent-activities` | GET |
| `/api/v1/stats/analytics` | POST |
| `/api/v1/owners/{ownerId}/summary` | GET |
| `/api/v1/stats/dashboard` | GET |

### Profile & Business Settings
| Endpoint | Method |
|----------|--------|
| `/api/v1/owners/{ownerId}` | GET |
| `/api/v1/owners/{ownerId}` | PATCH |
| `/api/v1/files/upload` | POST |
| `/api/v1/upi-accounts/owner/{ownerId}` | GET |
| `/api/v1/upi-accounts/owner/{ownerId}` | PATCH |
| `/api/v1/upi-accounts/owner/{ownerId}/verify` | POST |
| `/api/v1/documents` | GET |
| `/api/v1/documents` | POST |
| `/api/v1/documents/order` | PUT |
| `/api/v1/location-details/users/{userId}` | GET |
| `/api/v1/location-details/users/{userId}` | PATCH |

### Subscriptions & Verification
| Endpoint | Method |
|----------|--------|
| `/api/v1/payment/razorpay-config` | GET |
| `/api/v1/subscription/create` | POST |
| `/api/v1/payment/verify` | POST |
| `/api/v1/subscription/{ownerId}` | GET |
| `/api/v1/subscription/trial` | POST |

### Notifications & Support
| Endpoint | Method |
|----------|--------|
| `/api/v1/fcm/register` | POST |
| `/api/v1/fcm/unregister` | POST |
| `/api/v1/users/{userId}/notifications` | GET |
| `/api/v1/notifications/{id}` | GET |
| `/api/v1/notifications/{id}/read` | PATCH |
| `/api/v1/notifications/mark-read` | POST |
| `/api/v1/invoices/customer/{customerId}` | GET |
| `/api/v1/receipts/customer/{customerId}` | GET |
| `/api/v1/feedbacks/user/{userId}` | GET |
| `/api/v1/feedbacks` | POST |
| `/api/v1/app-usage-guide` | GET |

---

## 2. Connect (Customer App)
*15 endpoints — prefix: `/api/v1`*

| Endpoint | Method |
|----------|--------|
| `/api/v1/auth/phone/login` | POST |
| `/api/v1/auth/logout` | POST |
| `/api/v1/customers/me/summary` | GET |
| `/api/v1/fcm/register` | POST |
| `/api/v1/fcm/unregister` | POST |
| `/api/v1/owners/{id}` | GET |
| `/api/v1/owners/public` | GET |
| `/api/v1/upi-accounts/owner/{id}` | GET |
| `/api/v1/sale-items/customer/{id}` | GET |
| `/api/v1/payments/customer/{id}` | GET |
| `/api/v1/payments` | POST |
| `/api/v1/payments/{id}/status` | PATCH |
| `/api/v1/files/upload` | POST |
| `/api/v1/documents` | GET |
| `/api/v1/products/owner/{id}/with-stock` | GET |

---

## 3. Dashboard (Admin Panel)
*11 endpoints*

| Endpoint | Method | Notes |
|----------|--------|-------|
| `/api/v1/admin/owners` | GET | Called twice — once for login validation, once for listing |
| `/api/v1/admin/owners/{ownerId}` | GET | |
| `/api/v1/admin/owners/{ownerId}/subscription/upgrade` | POST | |
| `/api/v1/admin/owners/{ownerId}/subscription/downgrade` | POST | |
| `/api/v1/admin/feedbacks` | GET | |
| `/api/v1/admin/image-store` | GET | |
| `/api/v1/admin/image-store` | POST | |
| `/api/v1/admin/image-store/{id}` | PATCH | |
| `/api/v1/admin/owner-onboarding` | GET | |
| `/api/v1/admin/owner-onboarding/{id}/status` | PATCH | |
| `/api/v1/files/upload` | POST | Shared with other clients |

---

## Summary

| Client | Total APIs |
|--------|-----------|
| Abhedyam (Owner App) | 65 |
| Connect (Customer App) | 15 |
| Dashboard (Admin Panel) | 11 |
| **Total unique calls** | **91** |

### Shared Endpoints (used by multiple clients)
| Endpoint | Method | Used By |
|----------|--------|---------|
| `/api/v1/files/upload` | POST | Abhedyam, Connect, Dashboard |
| `/api/v1/fcm/register` | POST | Abhedyam, Connect |
| `/api/v1/fcm/unregister` | POST | Abhedyam, Connect |
| `/api/v1/owners/{ownerId}` | GET | Abhedyam, Connect |
| `/api/v1/upi-accounts/owner/{ownerId}` | GET | Abhedyam, Connect |
| `/api/v1/payments/customer/{id}` | GET | Abhedyam, Connect |
| `/api/v1/payments` | POST | Abhedyam, Connect |
| `/api/v1/payments/{id}/status` | PATCH | Abhedyam, Connect |
| `/api/v1/documents` | GET | Abhedyam, Connect |
