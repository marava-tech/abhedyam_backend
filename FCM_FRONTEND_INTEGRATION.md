# FCM Push Notifications - Flutter Integration Guide

## Overview
This guide explains how to integrate Firebase Cloud Messaging (FCM) push notifications in your Flutter applications.

## Package Names
- **Owner/Business App**: `tech.marava.abhedyam`
- **Customer App**: `tech.marava.abhedyamc`

## Backend API Endpoints

### Register FCM Token
**Endpoint**: `POST /api/v1/fcm/register`

**Request**:
```dart
{
  "token": "fcm_registration_token",
  "packageName": "tech.marava.abhedyamc",
  "deviceId": "device_unique_id",
  "deviceType": "android"
}
```

### Unregister FCM Token
**Endpoint**: `POST /api/v1/fcm/unregister`

**Request**:
```dart
{
  "token": "fcm_registration_token",
  "packageName": "tech.marava.abhedyamc"
}
```

## Flutter Implementation

### 1. Add Dependencies

Add to `pubspec.yaml`:
```yaml
dependencies:
  firebase_core: ^2.24.2
  firebase_messaging: ^14.7.9
  device_info_plus: ^9.1.0
  package_info_plus: ^5.0.1
```

### 2. Initialize Firebase

```dart
import 'package:firebase_core/firebase_core.dart';
import 'firebase_options.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(MyApp());
}
```

### 3. FCM Service Implementation

Create `lib/services/fcm_service.dart`:

```dart
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:package_info_plus/package_info_plus.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class FcmService {
  static final FirebaseMessaging _messaging = FirebaseMessaging.instance;
  static String? _fcmToken;
  
  static Future<void> initialize() async {
    NotificationSettings settings = await _messaging.requestPermission(
      alert: true,
      badge: true,
      sound: true,
    );
    
    if (settings.authorizationStatus == AuthorizationStatus.authorized) {
      _fcmToken = await _messaging.getToken();
      if (_fcmToken != null) {
        await registerToken(_fcmToken!);
      }
      
      _messaging.onTokenRefresh.listen((newToken) {
        _fcmToken = newToken;
        registerToken(newToken);
      });
    }
    
    FirebaseMessaging.onMessage.listen(_handleForegroundMessage);
    FirebaseMessaging.onMessageOpenedApp.listen(_handleBackgroundMessage);
    
    RemoteMessage? initialMessage = await _messaging.getInitialMessage();
    if (initialMessage != null) {
      _handleBackgroundMessage(initialMessage);
    }
  }
  
  static Future<void> registerToken(String token) async {
    try {
      PackageInfo packageInfo = await PackageInfo.fromPlatform();
      DeviceInfoPlugin deviceInfo = DeviceInfoPlugin();
      String deviceId = '';
      String deviceType = '';
      
      if (Platform.isAndroid) {
        AndroidDeviceInfo androidInfo = await deviceInfo.androidInfo;
        deviceId = androidInfo.id;
        deviceType = 'android';
      } else if (Platform.isIOS) {
        IosDeviceInfo iosInfo = await deviceInfo.iosInfo;
        deviceId = iosInfo.identifierForVendor ?? '';
        deviceType = 'ios';
      }
      
      final response = await http.post(
        Uri.parse('https://your-api.com/api/v1/fcm/register'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $userToken',
        },
        body: jsonEncode({
          'token': token,
          'packageName': packageInfo.packageName,
          'deviceId': deviceId,
          'deviceType': deviceType,
        }),
      );
      
      if (response.statusCode == 200) {
        print('FCM token registered successfully');
      }
    } catch (e) {
      print('Error registering FCM token: $e');
    }
  }
  
  static Future<void> unregisterToken(String token) async {
    try {
      PackageInfo packageInfo = await PackageInfo.fromPlatform();
      
      final response = await http.post(
        Uri.parse('https://your-api.com/api/v1/fcm/unregister'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $userToken',
        },
        body: jsonEncode({
          'token': token,
          'packageName': packageInfo.packageName,
        }),
      );
      
      if (response.statusCode == 200) {
        print('FCM token unregistered successfully');
      }
    } catch (e) {
      print('Error unregistering FCM token: $e');
    }
  }
  
  static void _handleForegroundMessage(RemoteMessage message) {
    print('Foreground message: ${message.notification?.title}');
    // Show local notification or update UI
  }
  
  static void _handleBackgroundMessage(RemoteMessage message) {
    print('Background message: ${message.notification?.title}');
    // Navigate to relevant screen
  }
  
  static Future<void> logout() async {
    if (_fcmToken != null) {
      await unregisterToken(_fcmToken!);
      _fcmToken = null;
    }
  }
}
```

### 4. Background Handler

Create `lib/firebase_messaging_handler.dart`:

```dart
import 'package:firebase_messaging/firebase_messaging.dart';

@pragma('vm:entry-point')
Future<void> firebaseMessagingBackgroundHandler(RemoteMessage message) async {
  print('Handling background message: ${message.messageId}');
}
```

Update `main.dart`:

```dart
import 'package:firebase_messaging/firebase_messaging.dart';
import 'firebase_messaging_handler.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  
  FirebaseMessaging.onBackgroundMessage(firebaseMessagingBackgroundHandler);
  
  await FcmService.initialize();
  
  runApp(MyApp());
}
```

### 5. Android Configuration

Add to `android/app/build.gradle`:
```gradle
dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
}
```

Create `android/app/src/main/AndroidManifest.xml` notification channel:
```xml
<meta-data
    android:name="com.google.firebase.messaging.default_notification_channel_id"
    android:value="reminders" />
```

### 6. iOS Configuration

Add to `ios/Runner/Info.plist`:
```xml
<key>FirebaseAppDelegateProxyEnabled</key>
<false/>
```

Enable push notifications in Xcode capabilities.

## Usage

```dart
// Initialize on app start
await FcmService.initialize();

// Unregister on logout
await FcmService.logout();
```

## Notification Payload

Reminders send notifications with:
```json
{
  "notification": {
    "title": "Reminder Name",
    "body": "Reminder Text"
  },
  "data": {
    "type": "reminder"
  }
}
```

## Testing

1. Register token via `/api/v1/fcm/register`
2. Create reminder with `channel: "IN_APP"`
3. Set reminder time to trigger notification
4. Verify notification received on device
