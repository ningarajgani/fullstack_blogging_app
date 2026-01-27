# 🚀 Complete DevOps Pipeline Documentation

## 📋 Pipeline Overview

This project implements a complete CI/CD pipeline for a Spring Boot application with the following stages:

```
GitHub → Jenkins → Maven → JaCoCo → SonarQube → Docker → Kubernetes
```

## 🛠️ Tools & Technologies

| Category | Tool | Purpose | Status |
|----------|------|---------|--------|
| **Source Control** | GitHub | Version control | ✅ Configured |
| **CI/CD** | Jenkins | Automation server | ✅ Configured |
| **Build Tool** | Maven | Build & dependency management | ✅ Configured |
| **Runtime** | JDK 17 | Java runtime | ✅ Configured |
| **Testing** | JUnit | Unit testing | ✅ Configured |
| **Coverage** | JaCoCo | Code coverage | ✅ Configured |
| **Code Quality** | SonarQube | Static analysis | 🔧 Fixed |
| **Security** | Trivy | Vulnerability scanning | ✅ Configured |
| **Containerization** | Docker | Container platform | ✅ Configured |
| **Registry** | Docker Hub | Image registry | 🔄 Next |
| **Orchestration** | Kubernetes | Container orchestration | 🔄 Next |
| **Artifact Repository** | Nexus | Artifact management | 🔄 Next |

## 🔧 SonarQube Configuration Fixed

### Issues Resolved:
1. ✅ **JaCoCo Plugin Enabled** - Uncommented and configured in pom.xml
2. ✅ **SonarQube Properties Added** - Added comprehensive properties in pom.xml
3. ✅ **Jenkinsfile Fixed** - Corrected shell commands and SonarQube integration
4. ✅ **Coverage Reports** - Properly configured JaCoCo XML reports
5. ✅ **Quality Gate** - Enhanced with proper error handling

### SonarQube Access:
- **URL**: http://localhost:9000
- **Default Credentials**: admin/admin
- **Project Key**: fullstack-blogging-app

## 📊 Pipeline Stages Explained

### 1. **Clean Workspace**
```groovy
cleanWs()
```
- Cleans Jenkins workspace for fresh build

### 2. **Git Checkout**
```groovy
git branch: 'main', credentialsId: 'git-cred', url: 'https://github.com/ningarajgani/fullstack_blogging_app.git'
```
- Pulls latest code from GitHub main branch

### 3. **Compile**
```bash
mvn clean compile
```
- Compiles Java source code

### 4. **Test & Coverage**
```bash
mvn test jacoco:report
```
- Runs unit tests
- Generates JaCoCo coverage report
- Publishes test results and coverage

### 5. **Trivy File System Scan**
```bash
trivy fs --severity HIGH,CRITICAL --format table -o trivy-fs-report.html .
```
- Scans source code for vulnerabilities
- Generates HTML report

### 6. **SonarQube Analysis**
```bash
mvn sonar:sonar -Dsonar.projectKey=fullstack-blogging-app
```
- Performs static code analysis
- Uploads results to SonarQube server
- Includes coverage and test reports

### 7. **Quality Gate**
```groovy
waitForQualityGate()
```
- Waits for SonarQube quality gate result
- Marks build as unstable if quality gate fails

### 8. **Package**
```bash
mvn clean package -DskipTests
```
- Creates executable JAR file
- Archives artifacts

### 9. **Docker Build**
```bash
docker build -t fullstack-blogging-app:${BUILD_NUMBER} .
docker tag fullstack-blogging-app:${BUILD_NUMBER} fullstack-blogging-app:latest
```
- Builds Docker image with build number tag
- Creates latest tag

### 10. **Trivy Docker Image Scan**
```bash
trivy image --severity HIGH,CRITICAL fullstack-blogging-app:latest
```
- Scans Docker image for vulnerabilities

## 🎯 Next Steps (Remaining Pipeline)

### Phase 2: Artifact Management
```groovy
stage('Publish to Nexus') {
    steps {
        nexusArtifactUploader(
            nexusVersion: 'nexus3',
            protocol: 'http',
            nexusUrl: '13.212.202.251:8081',
            groupId: 'com.example',
            version: '${BUILD_NUMBER}',
            repository: 'maven-releases',
            credentialsId: 'nexus-cred',
            artifacts: [
                [artifactId: 'twitter-app',
                 classifier: '',
                 file: 'target/twitter-app-0.0.3.jar',
                 type: 'jar']
            ]
        )
    }
}
```

### Phase 3: Container Registry
```groovy
stage('Push to Docker Hub') {
    steps {
        withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
            sh 'docker push ningarajgani/fullstack-blogging-app:${BUILD_NUMBER}'
            sh 'docker push ningarajgani/fullstack-blogging-app:latest'
        }
    }
}
```

### Phase 4: Kubernetes Deployment
```groovy
stage('Deploy to Kubernetes') {
    steps {
        withKubeConfig(caCertificate: '', clusterName: 'my-cluster', contextName: '', credentialsId: 'k8s-cred', namespace: 'default', serverUrl: '') {
            sh 'kubectl apply -f deployment-service.yml'
            sh 'kubectl get pods'
            sh 'kubectl get svc'
        }
    }
}
```

## 🔍 Troubleshooting SonarQube

### Common Issues & Solutions:

1. **Project Not Visible in SonarQube**
   ```bash
   # Check SonarQube logs
   docker logs sonarqube-container
   
   # Verify connection
   curl -u admin:admin http://localhost:9000/api/projects/search
   ```

2. **Coverage Not Showing**
   ```bash
   # Verify JaCoCo report exists
   ls -la target/site/jacoco/
   
   # Check SonarQube properties
   cat sonar-project.properties
   ```

3. **Quality Gate Timeout**
   ```groovy
   // Increase timeout in Jenkinsfile
   timeout(time: 10, unit: 'MINUTES')
   ```

### Troubleshooting Docker Permission Denied

If you see `permission denied while trying to connect to the Docker daemon socket`, it means the Jenkins container cannot access the host's Docker engine.

**Fix:**
Run this on your host machine to grant permission:
```bash
docker exec -u 0 -it <jenkins_container_id> chmod 666 /var/run/docker.sock
```

## 📈 Quality Metrics

### Current Targets:
- **Code Coverage**: > 80%
- **Bugs**: 0
- **Vulnerabilities**: 0
- **Code Smells**: < 10
- **Duplicated Lines**: < 3%

### SonarQube Quality Gate Rules:
```
Coverage on New Code: > 80%
Bugs: 0
Vulnerabilities: 0
Security Hotspots Reviewed: 100%
Maintainability Rating: A
Reliability Rating: A
Security Rating: A
```

## 🚀 Running the Pipeline

1. **Start SonarQube**:
   ```bash
   docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
   ```

2. **Configure Jenkins**:
   - Install SonarQube Scanner plugin
   - Configure SonarQube server in Jenkins
   - Add credentials for GitHub, SonarQube, Docker Hub

3. **Trigger Build**:
   - Push code to GitHub
   - Jenkins will automatically trigger the pipeline
   - Monitor progress in Jenkins dashboard

4. **Check Results**:
   - **Jenkins**: http://localhost:8080
   - **SonarQube**: http://localhost:9000
   - **Application**: http://localhost:8080 (after deployment)

## 📊 Pipeline Success Criteria

✅ **Build Success**: All stages complete without errors
✅ **Tests Pass**: All unit tests execute successfully  
✅ **Coverage**: Code coverage reports generated
✅ **Quality Gate**: SonarQube analysis completes
✅ **Security**: No critical vulnerabilities found
✅ **Artifacts**: JAR and Docker image created
✅ **Reports**: All reports archived and accessible

## 🎯 Interview Summary

*"We implemented a comprehensive Jenkins-based CI/CD pipeline that automatically checks out code from GitHub, builds and tests a Spring Boot application using Maven, measures code coverage with JaCoCo, performs static code analysis with SonarQube, scans for vulnerabilities using Trivy, containerizes the application with Docker, and prepares it for deployment to Kubernetes with proper quality gates and artifact management."*