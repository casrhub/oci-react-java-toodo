#!/bin/bash

# Customize this with your Docker Hub username
export DOCKER_USERNAME=casrhub
export IMAGE_NAME=todolistapp-springboot
export IMAGE_VERSION=0.1
export IMAGE=${DOCKER_USERNAME}/${IMAGE_NAME}:${IMAGE_VERSION}

# Ensure wallet files are in the correct location
echo "📦 Setting up wallet files..."
if [ ! -d "backend/Wallet_FATDATABASE" ]; then
    echo "Copying wallet files to backend directory..."
    cp -r ../Wallet_FATDATABASE backend/
fi

# Build backend JAR
echo "📦 Building Spring Boot app..."
cd backend || exit 1
mvn clean package spring-boot:repackage -DskipTests || exit 1
cd ..

# Build Docker image (for Docker Hub)
echo "🐳 Building Docker image: ${IMAGE}"
docker build -t $IMAGE -f backend/Dockerfile backend/

# Push to Docker Hub
echo "📤 Pushing Docker image to Docker Hub..."
docker push $IMAGE

# Optionally remove it locally (optional)
if [ $? -eq 0 ]; then
  echo "✅ Pushed successfully. Cleaning up local image..."
  docker rmi $IMAGE
else
  echo "❌ Docker push failed."
  exit 1
fi

echo "✅ Done! Image pushed to Docker Hub: $IMAGE"
