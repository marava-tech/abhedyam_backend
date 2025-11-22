#!/bin/bash

# Build and Push Script for Abhedyam Backend API
# Assumes JAR is already built in target folder
# Usage: ./build-and-push.sh

set -e

DOCKER_USERNAME="madhukinnera"
IMAGE_NAME="abhedyam"

echo "🐳 Building and pushing Docker image..."
echo "📦 Image: $DOCKER_USERNAME/$IMAGE_NAME:linux-amd64"

# Check if JAR exists
JAR_FILE=$(ls target/abhedyam-backend-*.jar 2>/dev/null | head -n 1)
if [ -z "$JAR_FILE" ]; then
    echo "❌ JAR file not found in target folder. Please build the project first:"
    echo "   mvn clean package"
    exit 1
fi
echo "✅ Found JAR: $JAR_FILE"

# Check Docker Hub authentication
if ! docker info 2>/dev/null | grep -q "Username"; then
    echo "❌ Not logged in to Docker Hub. Please login first:"
    echo "   docker login"
    exit 1
fi

echo "✅ Docker Hub authentication confirmed"

# Build and push
echo "🐧 Building Linux AMD64 image..."
docker buildx build \
    --platform linux/amd64 \
    --tag $DOCKER_USERNAME/$IMAGE_NAME:linux-amd64 \
    --push \
    .

echo "✅ Successfully built and pushed image!"
echo "🎉 Image available at: $DOCKER_USERNAME/$IMAGE_NAME:linux-amd64"
