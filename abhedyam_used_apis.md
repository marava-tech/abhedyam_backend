# Project APIs Usage Report

This file lists all the APIs integrated into the Abhedyam project, including their HTTP methods and usage frequency within the service layer.

## Authentication & Onboarding
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/auth/google/login` | POST | 1 |
| `https://s3.marava.tech/upload` | POST | 1 |
| `/api/v1/owner-onboarding` | POST | 1 |
| `/api/v1/owner-onboarding/owner/{ownerId}` | GET | 1 |

## Products & Inventory
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/owners/{ownerId}/products` | GET | 1 |
| `/api/v1/products` | POST | 1 |
| `/api/v1/stock/{productId}/current` | GET | 1 |
| `/api/v1/stock/update` | PUT | 1 |
| `/api/v1/image-store/search` | GET | 1 |

## Customers & CRM
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/owners/{ownerId}/customers` | GET | 1 |
| `/api/v1/customers/{customerId}` | GET | 2 |
| `/api/v1/customers` | POST | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/summary` | GET | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/sales-summary` | GET | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/payments-summary` | GET | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/notes-summary` | GET | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/reminders-summary` | GET | 1 |
| `/api/v1/location-details/customers/{customerId}` | GET | 1 |
| `/api/v1/notes/customer/{customerId}` | GET | 1 |
| `/api/v1/reminders/customer/{customerId}` | GET | 1 |
| `/api/v1/notes` | POST | 1 |
| `/api/v1/reminders` | POST | 1 |
| `/api/v1/owners/{ownerId}/customers/{customerId}/sale-items` | GET | 1 |
| `/api/v1/location-details/customers/{customerId}` | PATCH | 1 |
| `/api/v1/location-details/customers/locations` | POST | 1 |
| `/api/v1/location-details/villages` | GET | 1 |
| `/api/v1/customers/nearest` | POST | 1 |

## Sales & Payments
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/sales` | POST | 1 |
| `/api/v1/owners/{ownerId}/payments` | GET | 1 |
| `/api/v1/payments/customer/{customerId}` | GET | 1 |
| `/api/v1/payments` | POST | 1 |
| `/api/v1/payments/{paymentId}/status` | PATCH | 1 |

## Stats & Dashboard
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/stats` | GET | 1 |
| `/api/v1/daily-quotes/today` | GET | 1 |
| `/api/v1/stats/recent-activities` | GET | 1 |
| `/api/v1/stats/analytics` | POST | 1 |
| `/api/v1/owners/{ownerId}/summary` | GET | 1 |
| `/api/v1/stats/dashboard` | GET | 1 |

## Profile & Business Settings
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/owners/{ownerId}` | GET | 1 |
| `/api/v1/owners/{ownerId}` | PATCH | 1 |
| `/api/v1/files/upload` | POST | 1 |
| `/api/v1/upi-accounts/owner/{ownerId}` | GET | 1 |
| `/api/v1/upi-accounts/owner/{ownerId}` | PATCH | 1 |
| `/api/v1/upi-accounts/owner/{ownerId}/verify` | POST | 1 |
| `/api/v1/documents` | GET | 1 |
| `/api/v1/documents` | POST | 1 |
| `/api/v1/documents/order` | PUT | 1 |
| `/api/v1/location-details/users/{userId}` | GET | 1 |
| `/api/v1/location-details/users/{userId}` | PATCH | 1 |

## Subscriptions & Verification
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/payment/razorpay-config` | GET | 1 |
| `/api/v1/subscription/create` | POST | 1 |
| `/api/v1/payment/verify` | POST | 1 |
| `/api/v1/subscription/{ownerId}` | GET | 1 |
| `/api/v1/subscription/trial` | POST | 1 |

## Notifications & Support
| API Endpoint | Method | Project Usage |
|--------------|--------|---------------|
| `/api/v1/fcm/register` | POST | 1 |
| `/api/v1/fcm/unregister` | POST | 1 |
| `/api/v1/users/{userId}/notifications` | GET | 1 |
| `/api/v1/notifications/{id}` | GET | 1 |
| `/api/v1/notifications/{id}/read` | PATCH | 1 |
| `/api/v1/notifications/mark-read` | POST | 1 |
| `/api/v1/invoices/customer/$customerId` | GET | 1 |
| `/api/v1/receipts/customer/$customerId` | GET | 1 |
| `/api/v1/feedbacks/user/{userId}` | GET | 1 |
| `/api/v1/feedbacks` | POST | 1 |
| `/api/v1/app-usage-guide` | GET | 1 |

---
**Total APIs Integrated: 65**
