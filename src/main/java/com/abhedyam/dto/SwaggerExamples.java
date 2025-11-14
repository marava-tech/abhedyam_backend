package com.abhedyam.dto;

public class SwaggerExamples {
    
    public static final String SALE_CREATE_REQUEST = """
        {
          "customerId": "123e4567-e89b-12d3-a456-426614174000",
          "items": [
            {
              "productId": "123e4567-e89b-12d3-a456-426614174001",
              "price": 1000.00,
              "quantity": 2
            }
          ],
          "dueDate": "2024-12-31T23:59:59Z",
          "idempotencyKey": "unique-key-123"
        }
        """;
    
    public static final String CUSTOMER_CREATE_REQUEST = """
        {
          "name": "John Doe",
          "phone": "+919876543210",
          "email": "john@example.com",
          "address": "123 Main St, City"
        }
        """;
    
    public static final String PRODUCT_CREATE_REQUEST = """
        {
          "code": "PROD001",
          "name": "Sample Product",
          "price": 500.00,
          "stock": 100,
          "images": ["https://example.com/image1.jpg"]
        }
        """;
    
    public static final String OTP_SEND_REQUEST = """
        {
          "identifier": "user@example.com"
        }
        """;
    
    public static final String OTP_VERIFY_REQUEST = """
        {
          "identifier": "user@example.com",
          "otp": "1234"
        }
        """;
    
    public static final String REMINDER_CREATE_REQUEST = """
        {
          "customerId": "123e4567-e89b-12d3-a456-426614174000",
          "name": "Follow up call",
          "text": "Call customer about payment",
          "type": "FOLLOW_UP",
          "channel": "SMS",
          "dueAt": "2024-12-31T10:00:00Z"
        }
        """;
    
    public static final String NOTE_CREATE_REQUEST = """
        {
          "customerId": "123e4567-e89b-12d3-a456-426614174000",
          "text": "Customer prefers morning calls"
        }
        """;
    
    public static final String STOCK_ADJUSTMENT_REQUEST = """
        {
          "productId": "123e4567-e89b-12d3-a456-426614174001",
          "changeQty": 10,
          "note": "Stock correction"
        }
        """;
    
    public static final String PAYMENT_STATUS_UPDATE_REQUEST = """
        {
          "status": "SUCCESS",
          "reference": "TXN123456789"
        }
        """;
    
    public static final String UPI_PAYMENT_LINK_REQUEST = """
        {
          "customerId": "123e4567-e89b-12d3-a456-426614174000",
          "amount": 1000.00,
          "description": "Payment for order #123"
        }
        """;
}

