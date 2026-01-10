# App Usage Information API Specification

## Endpoint
```
GET /api/v1/app-usage-guide
```

## Response Structure

```json
{
  "success": true,
  "message": "App usage guide retrieved successfully",
  "data": {
    "version": "1.2.0",
    "lastUpdated": "2024-01-15T10:30:00Z",
    "sections": [
      {
        "id": "profile-management",
        "title": "Profile Management",
        "icon": "account_circle",
        "description": "Manage your business profile and personal information",
        "items": [
          {
            "id": "update-profile",
            "title": "Update Your Profile",
            "description": "Keep your business information up to date",
            "icon": "edit",
            "steps": [
              {
                "order": 1,
                "title": "Navigate to Profile",
                "description": "Tap on the profile icon in the bottom navigation or settings menu",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Edit Information",
                "description": "Click on 'Edit Profile' button to modify your details",
                "icon": "edit"
              },
              {
                "order": 3,
                "title": "Update Fields",
                "description": "You can update:\n• Business Name\n• Personal Details (Name, Phone, Email)\n• Profile Image\n• UPI Account Details",
                "icon": "info"
              },
              {
                "order": 4,
                "title": "Save Changes",
                "description": "Tap 'Save' to update your profile information",
                "icon": "save"
              }
            ],
            "tips": [
              "Upload a clear business logo for better brand recognition",
              "Keep your UPI details updated for easy payment collection"
            ]
          },
          {
            "id": "update-upi",
            "title": "Manage UPI Account",
            "description": "Add or update your UPI account for receiving payments",
            "icon": "account_balance_wallet",
            "steps": [
              {
                "order": 1,
                "title": "Go to Profile",
                "description": "Navigate to your profile section",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Select UPI Settings",
                "description": "Find and tap on 'UPI Account' section",
                "icon": "account_balance_wallet"
              },
              {
                "order": 3,
                "title": "Add/Edit UPI",
                "description": "Enter your UPI ID (e.g., yourname@paytm) and save",
                "icon": "edit"
              }
            ]
          }
        ]
      },
      {
        "id": "sales-management",
        "title": "Sales Management",
        "icon": "shopping_cart",
        "description": "Create and manage your sales transactions",
        "items": [
          {
            "id": "create-sale",
            "title": "Sell a Product",
            "description": "Record a new sale transaction",
            "icon": "add_shopping_cart",
            "steps": [
              {
                "order": 1,
                "title": "Tap Sell Button",
                "description": "Click on the 'Sell' button from the home screen or bottom navigation",
                "icon": "add_shopping_cart"
              },
              {
                "order": 2,
                "title": "Select Customer",
                "description": "Search and select an existing customer, or add a new customer by entering:\n• Customer Name\n• Phone Number\n• Village (optional)",
                "icon": "person_search"
              },
              {
                "order": 3,
                "title": "Add Products",
                "description": "Add products to the sale:\n• Search and select from your product list\n• Or add a custom product with name and price",
                "icon": "inventory"
              },
              {
                "order": 4,
                "title": "Complete Sale",
                "description": "Review the sale details and tap 'Create Sale' to record the transaction",
                "icon": "check_circle"
              }
            ],
            "tips": [
              "You can add multiple products in a single sale",
              "Custom products are useful for one-time items not in your inventory"
            ]
          },
          {
            "id": "view-sales",
            "title": "View Sales History",
            "description": "Track all your sales transactions",
            "icon": "history",
            "steps": [
              {
                "order": 1,
                "title": "Go to Sales",
                "description": "Navigate to the 'Sales' section from home screen",
                "icon": "shopping_cart"
              },
              {
                "order": 2,
                "title": "Browse Transactions",
                "description": "View all sales with customer details, products, and amounts",
                "icon": "list"
              },
              {
                "order": 3,
                "title": "Filter & Search",
                "description": "Use search to find specific sales or filter by date range",
                "icon": "search"
              }
            ]
          }
        ]
      },
      {
        "id": "customer-management",
        "title": "Customer Management",
        "icon": "people",
        "description": "Manage your customer relationships and track interactions",
        "items": [
          {
            "id": "view-customer-details",
            "title": "View Customer Details",
            "description": "Access comprehensive customer information",
            "icon": "person",
            "steps": [
              {
                "order": 1,
                "title": "Go to Customers",
                "description": "Tap on 'Customers' from the home screen",
                "icon": "people"
              },
              {
                "order": 2,
                "title": "Select Customer",
                "description": "Tap on any customer card to view their details",
                "icon": "person"
              },
              {
                "order": 3,
                "title": "Explore Information",
                "description": "View customer details including:\n• Contact Information\n• Sales History\n• Payment History\n• Call Logs\n• Reminders\n• Location Details",
                "icon": "info"
              }
            ]
          },
          {
            "id": "customer-payments",
            "title": "Track Payments",
            "description": "View and manage customer payments",
            "icon": "payments",
            "steps": [
              {
                "order": 1,
                "title": "Open Customer Details",
                "description": "Navigate to a customer's detail page",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "View Payment History",
                "description": "Scroll to 'Payment History' section to see all payments",
                "icon": "history"
              },
              {
                "order": 3,
                "title": "Add Payment",
                "description": "Tap 'Add Payment' to record a new payment for a sale",
                "icon": "add"
              }
            ],
            "tips": [
              "Payments can be recorded as Cash, UPI, PhonePe, Paytm, or from Abhedyam Connect app"
            ]
          },
          {
            "id": "customer-call-logs",
            "title": "Sync Call Logs",
            "description": "Automatically sync phone call history with customers",
            "icon": "call",
            "steps": [
              {
                "order": 1,
                "title": "Open Customer Details",
                "description": "Go to any customer's detail page",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Sync Calls",
                "description": "Tap 'Sync Calls' button to import call history from your phone",
                "icon": "sync"
              },
              {
                "order": 3,
                "title": "Grant Permissions",
                "description": "Allow phone call log access when prompted",
                "icon": "lock"
              },
              {
                "order": 4,
                "title": "View Call History",
                "description": "See all calls with this customer including date, time, and duration",
                "icon": "history"
              }
            ],
            "tips": [
              "Call logs help track customer communication history",
              "Only calls from last 30 days are synced"
            ]
          },
          {
            "id": "customer-reminders",
            "title": "Set Reminders",
            "description": "Create reminders for follow-ups and important tasks",
            "icon": "notifications",
            "steps": [
              {
                "order": 1,
                "title": "Open Customer Details",
                "description": "Navigate to the customer's detail page",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Go to Reminders",
                "description": "Scroll to 'Reminders' section",
                "icon": "notifications"
              },
              {
                "order": 3,
                "title": "Add Reminder",
                "description": "Tap 'Add Reminder' and fill in:\n• Reminder Name\n• Date & Time\n• Type (Call, Visit, Payment, etc.)\n• Channel (SMS, WhatsApp, Call)",
                "icon": "add_alarm"
              },
              {
                "order": 4,
                "title": "Get Notified",
                "description": "You'll receive notifications at the scheduled time",
                "icon": "notifications_active"
              }
            ]
          },
          {
            "id": "customer-location",
            "title": "Mark Customer Location",
            "description": "Save customer location for easy navigation",
            "icon": "location_on",
            "steps": [
              {
                "order": 1,
                "title": "Open Customer Details",
                "description": "Go to customer's detail page",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Mark Location",
                "description": "Tap 'Mark Location' button to save your current location",
                "icon": "my_location"
              },
              {
                "order": 3,
                "title": "Grant Permissions",
                "description": "Allow location access when prompted",
                "icon": "lock"
              },
              {
                "order": 4,
                "title": "Get Directions",
                "description": "Tap on village name or 'Directions' to open in Google Maps",
                "icon": "directions"
              }
            ]
          },
          {
            "id": "customer-access",
            "title": "View Customer POV",
            "description": "See how customers view their details in Abhedyam Connect app",
            "icon": "visibility",
            "steps": [
              {
                "order": 1,
                "title": "Open Customer Details",
                "description": "Navigate to customer's detail page",
                "icon": "person"
              },
              {
                "order": 2,
                "title": "Tap Access",
                "description": "Click on 'Access' button in the customer header",
                "icon": "open_in_new"
              },
              {
                "order": 3,
                "title": "View in Connect App",
                "description": "The Abhedyam Connect app will open showing customer's view",
                "icon": "smartphone"
              }
            ],
            "tips": [
              "Make sure Abhedyam Connect app is installed",
              "If not installed, you'll be redirected to Play Store"
            ]
          }
        ]
      },
      {
        "id": "product-management",
        "title": "Product Management",
        "icon": "inventory",
        "description": "Manage your product inventory",
        "items": [
          {
            "id": "add-product",
            "title": "Add a Product",
            "description": "Add products to your inventory",
            "icon": "add_box",
            "steps": [
              {
                "order": 1,
                "title": "Go to Products",
                "description": "Tap on 'Products' from the home screen",
                "icon": "inventory"
              },
              {
                "order": 2,
                "title": "Add New Product",
                "description": "Tap the '+' button to add a new product",
                "icon": "add"
              },
              {
                "order": 3,
                "title": "Enter Details",
                "description": "Fill in:\n• Product Name\n• Product Code (optional)\n• Price\n• Stock Quantity (optional)",
                "icon": "edit"
              },
              {
                "order": 4,
                "title": "Save Product",
                "description": "Tap 'Save' to add the product to your inventory",
                "icon": "save"
              }
            ]
          },
          {
            "id": "manage-stock",
            "title": "Update Stock",
            "description": "Update product stock quantities",
            "icon": "inventory_2",
            "steps": [
              {
                "order": 1,
                "title": "Open Products",
                "description": "Navigate to Products section",
                "icon": "inventory"
              },
              {
                "order": 2,
                "title": "Select Product",
                "description": "Tap on a product to view details",
                "icon": "inventory_2"
              },
              {
                "order": 3,
                "title": "Update Stock",
                "description": "Tap 'Update Stock' and enter new quantity",
                "icon": "edit"
              }
            ]
          }
        ]
      },
      {
        "id": "statistics",
        "title": "Statistics & Insights",
        "icon": "analytics",
        "description": "Track your business performance",
        "items": [
          {
            "id": "view-stats",
            "title": "View Statistics",
            "description": "Monitor your business metrics",
            "icon": "bar_chart",
            "steps": [
              {
                "order": 1,
                "title": "Go to Statistics",
                "description": "Tap on 'Statistics' from the home screen",
                "icon": "analytics"
              },
              {
                "order": 2,
                "title": "View Metrics",
                "description": "See:\n• Total Sales\n• Total Revenue\n• Total Customers\n• Pending Payments\n• Sales Trends",
                "icon": "insights"
              },
              {
                "order": 3,
                "title": "Filter by Date",
                "description": "Select date range to view statistics for specific periods",
                "icon": "date_range"
              }
            ]
          }
        ]
      },
      {
        "id": "notifications",
        "title": "Notifications",
        "icon": "notifications",
        "description": "Stay updated with important alerts",
        "items": [
          {
            "id": "view-notifications",
            "title": "View Notifications",
            "description": "Check your app notifications",
            "icon": "notifications",
            "steps": [
              {
                "order": 1,
                "title": "Open Notifications",
                "description": "Tap on the notification bell icon",
                "icon": "notifications"
              },
              {
                "order": 2,
                "title": "Browse Alerts",
                "description": "View reminders, payment alerts, and other notifications",
                "icon": "list"
              }
            ]
          }
        ]
      },
      {
        "id": "settings",
        "title": "Settings",
        "icon": "settings",
        "description": "Configure app settings and preferences",
        "items": [
          {
            "id": "app-settings",
            "title": "App Settings",
            "description": "Manage app preferences",
            "icon": "settings",
            "steps": [
              {
                "order": 1,
                "title": "Open Settings",
                "description": "Tap on 'Settings' from the menu or profile",
                "icon": "settings"
              },
              {
                "order": 2,
                "title": "Configure Options",
                "description": "Manage:\n• Theme (Light/Dark)\n• Notifications\n• App Updates\n• Privacy Settings",
                "icon": "tune"
              }
            ]
          }
        ]
      }
    ]
  }
}
```

## Data Models

### Section Model
```typescript
interface Section {
  id: string;                    // Unique identifier (e.g., "profile-management")
  title: string;                 // Section title (e.g., "Profile Management")
  icon: string;                  // Material icon name (e.g., "account_circle")
  description: string;            // Brief section description
  items: GuideItem[];             // List of guide items in this section
}
```

### GuideItem Model
```typescript
interface GuideItem {
  id: string;                    // Unique identifier (e.g., "update-profile")
  title: string;                 // Item title (e.g., "Update Your Profile")
  description: string;           // Brief description
  icon: string;                  // Material icon name
  steps: Step[];                 // Ordered list of steps
  tips?: string[];               // Optional tips array
}
```

### Step Model
```typescript
interface Step {
  order: number;                 // Step order (1, 2, 3, ...)
  title: string;                 // Step title
  description: string;           // Detailed step description (supports \n for line breaks)
  icon: string;                  // Material icon name
}
```

## Response Metadata
- `version`: App version this guide is for
- `lastUpdated`: ISO 8601 timestamp of last update

## Error Response
```json
{
  "success": false,
  "message": "Failed to retrieve app usage guide",
  "data": null
}
```

## Notes
1. All icon names should be valid Material Icons
2. Descriptions support `\n` for line breaks
3. Steps are ordered by the `order` field
4. Tips are optional and can be displayed as helpful hints
5. The API should be cached on the client side to reduce server load
6. Version field helps determine if guide needs update

