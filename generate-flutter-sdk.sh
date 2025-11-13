#!/bin/bash

# Script to generate Flutter/Dart client SDK from OpenAPI spec
# Requires: openapi-generator-cli (install via: npm install -g @openapitools/openapi-generator-cli)

set -e

API_URL="${API_URL:-http://localhost:8080/api/v1/api-docs}"
OUTPUT_DIR="${OUTPUT_DIR:-./generated/flutter-client}"

echo "Generating Flutter SDK from OpenAPI spec..."
echo "API URL: $API_URL"
echo "Output Directory: $OUTPUT_DIR"

openapi-generator-cli generate \
  -i "$API_URL" \
  -g dart \
  -o "$OUTPUT_DIR" \
  --additional-properties=serializationLibrary=json_serializable,dateLibrary=core,useEnumExtension=true,enumUnknownDefaultCase=true \
  --package-name=abhedyam_api \
  --pub-name=abhedyam_api \
  --pub-version=1.0.0 \
  --pub-description="Abhedyam Backend API Client for Flutter" \
  --pub-author="Abhedyam Team" \
  --pub-author-email="support@abhedyam.com" \
  --pub-homepage="https://github.com/abhedyam/flutter-client"

echo "Flutter SDK generated successfully in $OUTPUT_DIR"
echo "To use in Flutter project, add to pubspec.yaml:"
echo "  dependencies:"
echo "    abhedyam_api:"
echo "      path: $OUTPUT_DIR"

