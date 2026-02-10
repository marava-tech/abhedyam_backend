# Connect Used APIs

This document lists all the API endpoints used in the Abhedyam Connect (Customers) project for communication with the backend services.

| API Endpoint | Method | Usage Frequency |
| :--- | :--- | :---: |
| `/auth/phone/login` | POST | 1 |
| `/auth/logout` | POST | 1 |
| `/customers/me/summary` | GET | 1 |
| `/fcm/register` | POST | 1 |
| `/fcm/unregister` | POST | 1 |
| `/owners/{id}` | GET | 1 |
| `/owners/public` | GET | 1 |
| `/upi-accounts/owner/{id}` | GET | 1 |
| `/sale-items/customer/{id}` | GET | 1 |
| `/payments/customer/{id}` | GET | 1 |
| `/payments` | POST | 1 |
| `/payments/{id}/status` | PATCH | 1 |
| `/files/upload` | POST | 1 |
| `/documents` | GET | 1 |
| `/products/owner/{id}/with-stock` | GET | 1 |

---

### Summary
* **Total APIs:** 15
* **HTTP Methods Breakdown:**
    * **GET:** 9
    * **POST:** 5
    * **PATCH:** 1
