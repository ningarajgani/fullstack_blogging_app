#!/bin/bash

# SonarQube Setup Script for DevOps Pipeline
echo "🚀 Setting up SonarQube for DevOps Pipeline..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Stop and remove existing SonarQube container if exists
echo "🧹 Cleaning up existing SonarQube container..."
docker stop sonarqube 2>/dev/null || true
docker rm sonarqube 2>/dev/null || true

# Create SonarQube data directory
echo "📁 Creating SonarQube data directory..."
mkdir -p sonarqube_data

# Run SonarQube container
echo "🐳 Starting SonarQube container..."
docker run -d \
  --name sonarqube \
  -p 9000:9000 \
  -v $(pwd)/sonarqube_data:/opt/sonarqube/data \
  sonarqube:latest

# Wait for SonarQube to start
echo "⏳ Waiting for SonarQube to start (this may take a few minutes)..."
timeout=300
counter=0

while [ $counter -lt $timeout ]; do
    if curl -s http://localhost:9000/api/system/status | grep -q '"status":"UP"'; then
        echo "✅ SonarQube is ready!"
        break
    fi
    echo "⏳ Still waiting... ($counter/$timeout seconds)"
    sleep 10
    counter=$((counter + 10))
done

if [ $counter -ge $timeout ]; then
    echo "❌ SonarQube failed to start within $timeout seconds"
    echo "📋 Check logs with: docker logs sonarqube"
    exit 1
fi

# Display access information
echo ""
echo "🎉 SonarQube Setup Complete!"
echo "📊 Access SonarQube at: http://localhost:9000"
echo "🔐 Default credentials: admin/admin"
echo ""
echo "📋 Next Steps:"
echo "1. Open http://localhost:9000 in your browser"
echo "2. Login with admin/admin"
echo "3. Change the default password when prompted"
echo "4. Create a token for Jenkins integration"
echo "5. Configure Jenkins SonarQube plugin with the token"
echo ""
echo "🔧 Jenkins Configuration:"
echo "- Go to Manage Jenkins > Configure System"
echo "- Add SonarQube server: http://localhost:9000"
echo "- Add authentication token in credentials"
echo ""
echo "🚀 Ready to run your DevOps pipeline!"