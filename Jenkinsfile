pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        SONAR_HOST_URL = 'http://localhost:9000'
        SONAR_PROJECT_KEY = 'fullstack-blogging-app'
        SONAR_PROJECT_NAME = 'FullStack-Blogging-App'
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Git Checkout') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-cred',
                    url: 'https://github.com/ningarajgani/fullstack_blogging_app.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test & Coverage') {
            steps {
                sh 'mvn test jacoco:report'
            }
            post {
                always {
                    publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                    publishCoverage adapters: [jacocoAdapter('target/site/jacoco/jacoco.xml')], sourceFileResolver: sourceFiles('STORE_LAST_BUILD')
                }
            }
        }

        stage('Trivy File System Scan') {
            steps {
                sh 'trivy fs --severity HIGH,CRITICAL --format table -o trivy-fs-report.html . || true'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                        -Dsonar.projectName="${SONAR_PROJECT_NAME}" \
                        -Dsonar.host.url=${SONAR_HOST_URL} \
                        -Dsonar.java.coveragePlugin=jacoco \
                        -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                        -Dsonar.junit.reportPaths=target/surefire-reports \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.tests=src/test/java
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    script {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            echo "Quality Gate failed: ${qg.status}"
                            // Don't fail the build for now, just warn
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            echo "Quality Gate passed: ${qg.status}"
                        }
                    }
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh 'docker build -t fullstack-blogging-app:${BUILD_NUMBER} .'
                    sh 'docker tag fullstack-blogging-app:${BUILD_NUMBER} fullstack-blogging-app:latest'
                }
            }
        }

        stage('Trivy Docker Image Scan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --format table -o trivy-image-report.html fullstack-blogging-app:latest || true'
            }
        }
    }

    post {
        always {
            // Archive reports
            archiveArtifacts artifacts: 'trivy-*.html', allowEmptyArchive: true
            
            // Clean up Docker images to save space
            sh 'docker image prune -f || true'
        }
        success {
            echo '🎉 LOCAL CI PIPELINE COMPLETED SUCCESSFULLY'
            echo '📊 Check SonarQube dashboard at: http://localhost:9000'
            echo '🐳 Docker image built: fullstack-blogging-app:${BUILD_NUMBER}'
        }
        failure {
            echo '❌ LOCAL CI PIPELINE FAILED'
        }
        unstable {
            echo '⚠️ LOCAL CI PIPELINE COMPLETED WITH WARNINGS'
        }
    }
}
