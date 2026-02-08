# API Status Documentation

This file tracks the status and usage of all APIs in the Abhedyam Backend system, grouped by parent controller.

## Authentication (AuthController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/auth/google/login` | Login/Signup using Google OAuth | Implemented |
| `POST /api/v1/auth/phone/login` | Login/Signup using Phone number | Implemented |

## Admin (AdminController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/admin/owners` | List all owners with filters and pagination | Implemented |
| `GET /api/v1/admin/owners/{ownerId}` | Get detailed info for a specific owner | Implemented |
| `POST /api/v1/admin/owners/{ownerId}/subscription/upgrade` | Upgrade owner to PRO plan | Implemented |
| `POST /api/v1/admin/owners/{ownerId}/subscription/downgrade` | Downgrade owner to GO plan | Implemented |
| `GET /api/v1/admin/feedbacks` | List all user feedbacks | Implemented |

## Owner Onboarding (OwnerOnboardingController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/owner-onboarding` | Submit a new onboarding request | Implemented |
| `PATCH /api/v1/owner-onboarding/{id}/status` | Update onboarding request status (Admin) | Implemented |
| `GET /api/v1/owner-onboarding/{id}` | Get details of a specific request | Implemented |
| `GET /api/v1/owner-onboarding/owner/{ownerId}` | Get all requests for a specific owner | Implemented |
| `GET /api/v1/owner-onboarding` | List all onboarding requests | Implemented |

## Sales (SaleController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/sales` | Create a new sale transaction | Implemented |
| `GET /api/v1/sales/transaction/{transactionId}` | Get sale details by transaction ID | Implemented |
| `GET /api/v1/sales/search` | Search sales with filters | Implemented |
| `GET /api/v1/sales/transaction/{transactionId}/items` | Get items in a specific transaction | Implemented |
| `POST /api/v1/sales/transaction/{transactionId}/cancel` | Cancel a sale transaction | Implemented |

## Products (ProductController & OwnerProductController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/products` | Create a new product | Implemented |
| `GET /api/v1/products/{id}` | Get product details | Implemented |
| `GET /api/v1/products/owner/{ownerId}/with-stock` | Get products with current stock levels | Implemented |
| `GET /api/v1/owners/{ownerId}/products` | List/Search owner products | Implemented |
| `PATCH /api/v1/owners/{ownerId}/products/{productId}` | Update product details | Implemented |
| `PATCH /api/v1/owners/{ownerId}/products/{productId}/toggle-active` | Toggle product active status | Implemented |

## Customers (CustomerController & OwnerCustomerController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/customers` | Create a new customer | Implemented |
| `GET /api/v1/customers/{id}` | Get customer profile | Implemented |
| `GET /api/v1/customers/search` | Search customers | Implemented |
| `GET /api/v1/owners/{ownerId}/customers` | List/Search owner customers | Implemented |
| `GET /api/v1/owners/{ownerId}/customers/{customerId}/dashboard` | Get customer analytics dashboard | Implemented |

## Stats & Analytics (StatsController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/stats` | Get daily statistics | Implemented |
| `POST /api/v1/stats/recompute` | Recompute stats for a date range | Implemented |
| `GET /api/v1/stats/dashboard` | Get summary dashboard stats | Implemented |
| `GET /api/v1/stats/recent-activities` | Get feed of recent activities | Implemented |
| `GET /api/v1/stats/analytics` | Get detailed analytics (Weekly/Monthly/Yearly) | Implemented |

## Inventory & Stock (InventoryController & StockController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/inventory/owner/{ownerId}` | Get all inventory items for owner | Implemented |
| `POST /api/v1/stock/purchase-in` | Record stock purchase (Stock In) | Implemented |
| `POST /api/v1/stock/sale-out` | Record manual sale (Stock Out) | Implemented |
| `POST /api/v1/stock/adjust` | Manual stock adjustment | Implemented |
| `PUT /api/v1/stock/update` | Bulk stock update | Implemented |
| `GET /api/v1/stock/{productId}/current` | Get current stock for product | Implemented |
| `GET /api/v1/stock/low-stock` | List low stock products | Implemented |

## Location & Villages (LocationDetailsController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/location-details/search-villages` | Search villages | Implemented |
| `GET /api/v1/location-details/villages` | List all villages with customer counts | Implemented |
| `POST /api/v1/location-details/customers/locations` | Get coordinates for multiple customers | Implemented |
| `PATCH /api/v1/location-details/customers/{customerId}` | Update customer location | Implemented |

## Payments (PaymentController & OwnerPaymentController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/payments` | Create a new payment record | Implemented |
| `GET /api/v1/payments/{paymentId}` | Get payment details | Implemented |
| `POST /api/v1/payments/verify` | Verify payment status | Implemented |
| `GET /api/v1/owners/{ownerId}/payments` | List owner payments | Implemented |

## Notifications (NotificationController & UserNotificationController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/notifications/{id}` | Get notification by ID | Implemented |
| `PATCH /api/v1/notifications/{id}/read` | Mark single notification as read | Implemented |
| `POST /api/v1/notifications/mark-read` | Bulk mark notifications as read | Implemented |
| `GET /api/v1/users/{userId}/notifications` | List notifications for user | Implemented |

## Customer Profile Details (SaleItem, Note, Reminder, Document)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/sale-items/{id}` | Get specific sale item details | Implemented |
| `GET /api/v1/sale-items/customer/{customerId}` | List all sale items for a customer | Implemented |
| `GET /api/v1/sale-items/transaction/{transactionId}` | Get items by transaction ID | Implemented |
| `GET /api/v1/notes/customer/{customerId}` | List all notes for a customer | Implemented |
| `POST /api/v1/notes` | Create a new note | Implemented |
| `GET /api/v1/reminders/customer/{customerId}` | List all reminders for a customer | Implemented |
| `POST /api/v1/reminders` | Create a new reminder | Implemented |
| `GET /api/v1/reminders/pending` | List all pending reminders | Implemented |
| `PATCH /api/v1/reminders/{id}/mark-sent` | Mark reminder as sent | Implemented |
| `GET /api/v1/documents` | List all available documents | Implemented |
| `POST /api/v1/documents` | Create/Upload a new document | Implemented |

## Subscription & Billing (SubscriptionController & InvoiceReceiptController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/subscription/create` | Create a new subscription | Implemented |
| `POST /api/v1/subscription/trial` | Start a trial subscription | Implemented |
| `GET /api/v1/user/subscription` | Get current user's subscription details | Implemented |
| `GET /api/v1/invoices/customer/{customerId}` | Get digital invoice for customer | Implemented |
| `GET /api/v1/receipts/customer/{customerId}` | Get payment receipt for customer | Implemented |

## UPI & Bank Accounts (UpiAccountController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/upi-accounts` | Add a new UPI account | Implemented |
| `GET /api/v1/upi-accounts/owner/{ownerId}` | List UPI accounts for owner | Implemented |
| `PATCH /api/v1/upi-accounts/owner/{ownerId}` | Update owner's UPI accounts | Implemented |
| `PUT /api/v1/upi-accounts/{id}/primary` | Set UPI account as primary | Implemented |

## Feedback & Support (FeedbackController & AppUsageGuideController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/feedbacks` | Submit user feedback/issue | Implemented |
| `GET /api/v1/feedbacks/user/{userId}` | List feedbacks by user | Implemented |
| `GET /api/v1/app-usage-guide` | Get application usage guide content | Implemented |

## Utility & System (DailyQuoteController, FcmController, CacheController, HealthController)
| API | Use | Status |
| :--- | :--- | :--- |
| `GET /api/v1/daily-quotes/today` | Get daily inspirational quote | Implemented |
| `POST /api/v1/fcm/register` | Register FCM token for push notifications | Implemented |
| `POST /api/v1/fcm/unregister` | Unregister FCM token | Implemented |
| `GET /api/v1/health` | Check backend service health | Implemented |
| `POST /api/v1/cache/invalidate` | Clear system cache | Implemented |
| `POST /api/v1/files/upload` | Generic file upload service | Implemented |
| `POST /api/v1/admin/image-store` | Create image store entry (Admin Key) | Implemented |
| `GET /api/v1/admin/image-store` | List all image store entries (Admin Key) | Implemented |
| `GET /api/v1/admin/image-store/{id}` | Get image store entry by ID (Admin Key) | Implemented |
| `PATCH /api/v1/admin/image-store/{id}` | Update image store entry (Admin Key) | Implemented |
| `DELETE /api/v1/admin/image-store/{id}` | Delete image store entry (Admin Key) | Implemented |
| `GET /api/v1/image-store/search` | Search image store by name or tags (JWT) | Implemented |
 
## Video Store (VideoStoreController)
| API | Use | Status |
| :--- | :--- | :--- |
| `POST /api/v1/admin/video-store` | Create video store entry (Admin Key) | Implemented |
| `GET /api/v1/admin/video-store` | List all video store entries (Admin Key) | Implemented |
| `GET /api/v1/admin/video-store/{id}` | Get video store entry by ID (Admin Key) | Implemented |
| `PATCH /api/v1/admin/video-store/{id}` | Update video store entry (Admin Key) | Implemented |
| `DELETE /api/v1/admin/video-store/{id}` | Delete video store entry (Admin Key) | Implemented |
| `GET /api/v1/video-store/search` | Search video store by name or tags (JWT) | Implemented |
