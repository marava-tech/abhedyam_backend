package com.abhedyam.service;

import com.abhedyam.dto.AppUsageGuideResponse;
import com.abhedyam.dto.GuideItem;
import com.abhedyam.dto.Section;
import com.abhedyam.dto.Step;
import com.abhedyam.service.interfaces.IAppUsageGuideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AppUsageGuideService implements IAppUsageGuideService {
    
    @Override
    public AppUsageGuideResponse getAppUsageGuide() {
        return new AppUsageGuideResponse(
            "1.2.0",
            Instant.parse("2024-01-15T10:30:00Z"),
            buildSections()
        );
    }
    
    private List<Section> buildSections() {
        return Arrays.asList(
            buildProfileManagementSection(),
            buildSalesManagementSection(),
            buildCustomerManagementSection(),
            buildProductManagementSection(),
            buildStatisticsSection(),
            buildNotificationsSection(),
            buildSettingsSection()
        );
    }
    
    private Section buildProfileManagementSection() {
        return new Section(
            "profile-management",
            "Profile Management",
            "account_circle",
            "Manage your business profile and personal information",
            Arrays.asList(
                new GuideItem(
                    "update-profile",
                    "Update Your Profile",
                    "Keep your business information up to date",
                    "edit",
                    Arrays.asList(
                        new Step(1, "Navigate to Profile", "Tap on the profile icon in the bottom navigation or settings menu", "person"),
                        new Step(2, "Edit Information", "Click on 'Edit Profile' button to modify your details", "edit"),
                        new Step(3, "Update Fields", "You can update:\n• Business Name\n• Personal Details (Name, Phone, Email)\n• Profile Image\n• UPI Account Details", "info"),
                        new Step(4, "Save Changes", "Tap 'Save' to update your profile information", "save")
                    ),
                    Arrays.asList(
                        "Upload a clear business logo for better brand recognition",
                        "Keep your UPI details updated for easy payment collection"
                    )
                ),
                new GuideItem(
                    "update-upi",
                    "Manage UPI Account",
                    "Add or update your UPI account for receiving payments",
                    "account_balance_wallet",
                    Arrays.asList(
                        new Step(1, "Go to Profile", "Navigate to your profile section", "person"),
                        new Step(2, "Select UPI Settings", "Find and tap on 'UPI Account' section", "account_balance_wallet"),
                        new Step(3, "Add/Edit UPI", "Enter your UPI ID (e.g., yourname@paytm) and save", "edit")
                    ),
                    null
                )
            )
        );
    }
    
    private Section buildSalesManagementSection() {
        return new Section(
            "sales-management",
            "Sales Management",
            "shopping_cart",
            "Create and manage your sales transactions",
            Arrays.asList(
                new GuideItem(
                    "create-sale",
                    "Sell a Product",
                    "Record a new sale transaction",
                    "add_shopping_cart",
                    Arrays.asList(
                        new Step(1, "Tap Sell Button", "Click on the 'Sell' button from the home screen or bottom navigation", "add_shopping_cart"),
                        new Step(2, "Select Customer", "Search and select an existing customer, or add a new customer by entering:\n• Customer Name\n• Phone Number\n• Village (optional)", "person_search"),
                        new Step(3, "Add Products", "Add products to the sale:\n• Search and select from your product list\n• Or add a custom product with name and price", "inventory"),
                        new Step(4, "Complete Sale", "Review the sale details and tap 'Create Sale' to record the transaction", "check_circle")
                    ),
                    Arrays.asList(
                        "You can add multiple products in a single sale",
                        "Custom products are useful for one-time items not in your inventory"
                    )
                ),
                new GuideItem(
                    "view-sales",
                    "View Sales History",
                    "Track all your sales transactions",
                    "history",
                    Arrays.asList(
                        new Step(1, "Go to Sales", "Navigate to the 'Sales' section from home screen", "shopping_cart"),
                        new Step(2, "Browse Transactions", "View all sales with customer details, products, and amounts", "list"),
                        new Step(3, "Filter & Search", "Use search to find specific sales or filter by date range", "search")
                    ),
                    null
                )
            )
        );
    }
    
    private Section buildCustomerManagementSection() {
        return new Section(
            "customer-management",
            "Customer Management",
            "people",
            "Manage your customer relationships and track interactions",
            Arrays.asList(
                new GuideItem(
                    "view-customer-details",
                    "View Customer Details",
                    "Access comprehensive customer information",
                    "person",
                    Arrays.asList(
                        new Step(1, "Go to Customers", "Tap on 'Customers' from the home screen", "people"),
                        new Step(2, "Select Customer", "Tap on any customer card to view their details", "person"),
                        new Step(3, "Explore Information", "View customer details including:\n• Contact Information\n• Sales History\n• Payment History\n• Call Logs\n• Reminders\n• Location Details", "info")
                    ),
                    null
                ),
                new GuideItem(
                    "customer-payments",
                    "Track Payments",
                    "View and manage customer payments",
                    "payments",
                    Arrays.asList(
                        new Step(1, "Open Customer Details", "Navigate to a customer's detail page", "person"),
                        new Step(2, "View Payment History", "Scroll to 'Payment History' section to see all payments", "history"),
                        new Step(3, "Add Payment", "Tap 'Add Payment' to record a new payment for a sale", "add")
                    ),
                    Arrays.asList(
                        "Payments can be recorded as Cash, UPI, PhonePe, Paytm, or from Abhedyam Connect app"
                    )
                ),
                new GuideItem(
                    "customer-call-logs",
                    "Sync Call Logs",
                    "Automatically sync phone call history with customers",
                    "call",
                    Arrays.asList(
                        new Step(1, "Open Customer Details", "Go to any customer's detail page", "person"),
                        new Step(2, "Sync Calls", "Tap 'Sync Calls' button to import call history from your phone", "sync"),
                        new Step(3, "Grant Permissions", "Allow phone call log access when prompted", "lock"),
                        new Step(4, "View Call History", "See all calls with this customer including date, time, and duration", "history")
                    ),
                    Arrays.asList(
                        "Call logs help track customer communication history",
                        "Only calls from last 30 days are synced"
                    )
                ),
                new GuideItem(
                    "customer-reminders",
                    "Set Reminders",
                    "Create reminders for follow-ups and important tasks",
                    "notifications",
                    Arrays.asList(
                        new Step(1, "Open Customer Details", "Navigate to the customer's detail page", "person"),
                        new Step(2, "Go to Reminders", "Scroll to 'Reminders' section", "notifications"),
                        new Step(3, "Add Reminder", "Tap 'Add Reminder' and fill in:\n• Reminder Name\n• Date & Time\n• Type (Call, Visit, Payment, etc.)\n• Channel (SMS, WhatsApp, Call)", "add_alarm"),
                        new Step(4, "Get Notified", "You'll receive notifications at the scheduled time", "notifications_active")
                    ),
                    null
                ),
                new GuideItem(
                    "customer-location",
                    "Mark Customer Location",
                    "Save customer location for easy navigation",
                    "location_on",
                    Arrays.asList(
                        new Step(1, "Open Customer Details", "Go to customer's detail page", "person"),
                        new Step(2, "Mark Location", "Tap 'Mark Location' button to save your current location", "my_location"),
                        new Step(3, "Grant Permissions", "Allow location access when prompted", "lock"),
                        new Step(4, "Get Directions", "Tap on village name or 'Directions' to open in Google Maps", "directions")
                    ),
                    null
                ),
                new GuideItem(
                    "customer-access",
                    "View Customer POV",
                    "See how customers view their details in Abhedyam Connect app",
                    "visibility",
                    Arrays.asList(
                        new Step(1, "Open Customer Details", "Navigate to customer's detail page", "person"),
                        new Step(2, "Tap Access", "Click on 'Access' button in the customer header", "open_in_new"),
                        new Step(3, "View in Connect App", "The Abhedyam Connect app will open showing customer's view", "smartphone")
                    ),
                    Arrays.asList(
                        "Make sure Abhedyam Connect app is installed",
                        "If not installed, you'll be redirected to Play Store"
                    )
                )
            )
        );
    }
    
    private Section buildProductManagementSection() {
        return new Section(
            "product-management",
            "Product Management",
            "inventory",
            "Manage your product inventory",
            Arrays.asList(
                new GuideItem(
                    "add-product",
                    "Add a Product",
                    "Add products to your inventory",
                    "add_box",
                    Arrays.asList(
                        new Step(1, "Go to Products", "Tap on 'Products' from the home screen", "inventory"),
                        new Step(2, "Add New Product", "Tap the '+' button to add a new product", "add"),
                        new Step(3, "Enter Details", "Fill in:\n• Product Name\n• Product Code (optional)\n• Price\n• Stock Quantity (optional)", "edit"),
                        new Step(4, "Save Product", "Tap 'Save' to add the product to your inventory", "save")
                    ),
                    null
                ),
                new GuideItem(
                    "manage-stock",
                    "Update Stock",
                    "Update product stock quantities",
                    "inventory_2",
                    Arrays.asList(
                        new Step(1, "Open Products", "Navigate to Products section", "inventory"),
                        new Step(2, "Select Product", "Tap on a product to view details", "inventory_2"),
                        new Step(3, "Update Stock", "Tap 'Update Stock' and enter new quantity", "edit")
                    ),
                    null
                )
            )
        );
    }
    
    private Section buildStatisticsSection() {
        return new Section(
            "statistics",
            "Statistics & Insights",
            "analytics",
            "Track your business performance",
            Arrays.asList(
                new GuideItem(
                    "view-stats",
                    "View Statistics",
                    "Monitor your business metrics",
                    "bar_chart",
                    Arrays.asList(
                        new Step(1, "Go to Statistics", "Tap on 'Statistics' from the home screen", "analytics"),
                        new Step(2, "View Metrics", "See:\n• Total Sales\n• Total Revenue\n• Total Customers\n• Pending Payments\n• Sales Trends", "insights"),
                        new Step(3, "Filter by Date", "Select date range to view statistics for specific periods", "date_range")
                    ),
                    null
                )
            )
        );
    }
    
    private Section buildNotificationsSection() {
        return new Section(
            "notifications",
            "Notifications",
            "notifications",
            "Stay updated with important alerts",
            Arrays.asList(
                new GuideItem(
                    "view-notifications",
                    "View Notifications",
                    "Check your app notifications",
                    "notifications",
                    Arrays.asList(
                        new Step(1, "Open Notifications", "Tap on the notification bell icon", "notifications"),
                        new Step(2, "Browse Alerts", "View reminders, payment alerts, and other notifications", "list")
                    ),
                    null
                )
            )
        );
    }
    
    private Section buildSettingsSection() {
        return new Section(
            "settings",
            "Settings",
            "settings",
            "Configure app settings and preferences",
            Arrays.asList(
                new GuideItem(
                    "app-settings",
                    "App Settings",
                    "Manage app preferences",
                    "settings",
                    Arrays.asList(
                        new Step(1, "Open Settings", "Tap on 'Settings' from the menu or profile", "settings"),
                        new Step(2, "Configure Options", "Manage:\n• Theme (Light/Dark)\n• Notifications\n• App Updates\n• Privacy Settings", "tune")
                    ),
                    null
                )
            )
        );
    }
}

