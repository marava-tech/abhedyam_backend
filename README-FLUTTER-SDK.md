# Flutter SDK Generation

This document describes how to generate the Flutter/Dart client SDK from the OpenAPI specification.

## Prerequisites

1. Install OpenAPI Generator CLI:
   ```bash
   npm install -g @openapitools/openapi-generator-cli
   ```

2. Ensure the backend server is running:
   ```bash
   mvn spring-boot:run
   ```

## Generate SDK

### Option 1: Using the provided script
```bash
chmod +x generate-flutter-sdk.sh
./generate-flutter-sdk.sh
```

### Option 2: Manual generation
```bash
openapi-generator-cli generate \
  -i http://localhost:8080/api/v1/api-docs \
  -g dart \
  -o ./generated/flutter-client \
  --additional-properties=serializationLibrary=json_serializable,dateLibrary=core,useEnumExtension=true \
  --package-name=abhedyam_api \
  --pub-name=abhedyam_api \
  --pub-version=1.0.0
```

## Using in Flutter Project

1. Copy the generated SDK to your Flutter project or add as a local dependency:
   ```yaml
   # pubspec.yaml
   dependencies:
     abhedyam_api:
       path: ../abhedyam_backend/generated/flutter-client
   ```

2. Install dependencies:
   ```bash
   flutter pub get
   ```

3. Use in your Flutter code:
   ```dart
   import 'package:abhedyam_api/api.dart';
   
   final api = DefaultApi();
   api.setAccessToken('your-jwt-token');
   final response = await api.getCustomers();
   ```

## Regenerating SDK

After API changes, regenerate the SDK:
```bash
./generate-flutter-sdk.sh
```

## Configuration

You can customize the generation by modifying `openapi-generator-config.yaml` or passing additional properties to the generator.

