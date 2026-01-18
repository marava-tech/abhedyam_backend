# API Changes (Owner/User Scoped)

Base URL: `http://localhost:8080`
Auth: `Authorization: Bearer <token>`

## Consolidated Endpoints

### Products (list/search/paginate)
`GET /api/v1/owners/{ownerId}/products`

Query params: `q`, `isActive`, `page`, `size`, `sortBy`, `sortDirection`

Example:
```
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/00000000-0000-0000-0000-000000000001/products?q=rice&isActive=true&page=0&size=20&sortBy=createdAt&sortDirection=DESC"
```

Sample response:
```
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "11111111-1111-1111-1111-111111111111",
        "code": "PROD-1234",
        "name": "Rice",
        "price": 50.0,
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "isActive": true,
        "createdAt": "2026-01-18T10:00:00Z",
        "updatedAt": "2026-01-18T10:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### Customers (list/search/paginate with village)
`GET /api/v1/owners/{ownerId}/customers`

Query params: `q` (name/phone/village), `page`, `size`, `sortBy`, `sortDirection`

Example:
```
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/00000000-0000-0000-0000-000000000001/customers?q=madhapur&page=0&size=20"
```

Sample response:
```
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "22222222-2222-2222-2222-222222222222",
        "name": "Kiran",
        "phone": "9876543210",
        "phoneNormalized": "919876543210",
        "email": "kiran@example.com",
        "imageUrl": null,
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "village": "Madhapur",
        "createdAt": "2026-01-18T10:00:00Z",
        "updatedAt": "2026-01-18T10:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

### Payments (list/search/paginate)
`GET /api/v1/owners/{ownerId}/payments`

Query params: `q`, `page`, `size`, `sortBy`, `sortDirection`, `expand=names`

Example (core list):
```
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/00000000-0000-0000-0000-000000000001/payments?page=0&size=20"
```

Example (names expansion):
```
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/00000000-0000-0000-0000-000000000001/payments?page=0&size=20&expand=names"
```

Sample response (with expand):
```
{
  "success": true,
  "data": {
    "content": [
      {
        "id": "33333333-3333-3333-3333-333333333333",
        "customerId": "22222222-2222-2222-2222-222222222222",
        "customerName": "Kiran",
        "ownerId": "00000000-0000-0000-0000-000000000001",
        "saleItemId": "44444444-4444-4444-4444-444444444444",
        "productName": "Rice",
        "amount": 200.0,
        "medium": "UPI",
        "timestamp": "2026-01-18T10:00:00Z",
        "reference": "ORDER_ABC123",
        "status": "SUCCESS",
        "createdAt": "2026-01-18T10:00:00Z",
        "updatedAt": "2026-01-18T10:00:00Z"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

## Customer Summary Split (Performance)

### Basic summary
`GET /api/v1/owners/{ownerId}/customers/{customerId}/summary`

### Sales summary
`GET /api/v1/owners/{ownerId}/customers/{customerId}/sales-summary`

### Payments summary
`GET /api/v1/owners/{ownerId}/customers/{customerId}/payments-summary`

### Notes summary
`GET /api/v1/owners/{ownerId}/customers/{customerId}/notes-summary`

### Reminders summary
`GET /api/v1/owners/{ownerId}/customers/{customerId}/reminders-summary`

Sample response (basic summary):
```
{
  "success": true,
  "data": {
    "customerId": "22222222-2222-2222-2222-222222222222",
    "name": "Kiran",
    "phone": "9876543210",
    "imageUrl": null
  }
}
```

## Sale Items (owner-scoped)
`GET /api/v1/owners/{ownerId}/customers/{customerId}/sale-items`

Optional: `expand=product` to include product name.

Example:
```
curl -H "Authorization: Bearer <token>" \
  "http://localhost:8080/api/v1/owners/00000000-0000-0000-0000-000000000001/customers/22222222-2222-2222-2222-222222222222/sale-items?expand=product"
```

## Owner/User Scoped Replacements for `/me`

### Owner settings
`GET /api/v1/owners/{ownerId}/settings`
`PATCH /api/v1/owners/{ownerId}/settings`

### Owner update
`PATCH /api/v1/owners/{ownerId}`

### User update
`PATCH /api/v1/users/{userId}`

### Notifications
`GET /api/v1/users/{userId}/notifications`

### UPI account
`PATCH /api/v1/upi-accounts/owner/{ownerId}`
`POST /api/v1/upi-accounts/owner/{ownerId}/verify`

### Location details
`PATCH /api/v1/location-details/users/{userId}`

## Removed Endpoints (Breaking)

Products:
- `GET /api/v1/products/my-products`
- `GET /api/v1/products/search`
- `GET /api/v1/products/search-by-name`

Customers:
- `GET /api/v1/customers/my-customers`
- `GET /api/v1/customers/filter`
- `GET /api/v1/customers/search-by-name`
- `GET /api/v1/customers/{customerId}/summary`
- `PATCH /api/v1/customers/me`
- `GET /api/v1/customers/me/summary`

Payments:
- `GET /api/v1/payments/my-payments`
- `GET /api/v1/payments/filter`

Sale items:
- `GET /api/v1/sale-items/customer/{customerId}`

Other `/me` removals:
- `GET /api/v1/location-details/me`
- `PATCH /api/v1/location-details/me`
- `PATCH /api/v1/upi-accounts/me`
- `POST /api/v1/upi-accounts/verify/me`
- `GET /api/v1/owners/me/details`
- `PATCH /api/v1/owners/me`
- `GET /api/v1/owner-settings/me`
- `PATCH /api/v1/owner-settings/me`
- `GET /api/v1/notifications/me`
- `PATCH /api/v1/users/me`

