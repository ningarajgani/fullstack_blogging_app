pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    environment {
        SCANNER_HOME = tool 'sonar-scanner'
        SONAR_HOST_URL = 'http://sonarqube:9000'
        SONAR_PROJECT_KEY = 'fullstack-blogging-app'
        SONAR_PROJECT_NAME = 'FullStack-Blogging-App'
        MAVEN_OPTS = "-Dmaven.test.failure.ignore=false"
        APP_VERSION = '1.0.0-SNAPSHOT' // Added APP_VERSION
    }

    stages {

        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main',
                    credentialsId: 'git-cred',
                    url: 'https://github.com/ningarajgani/fullstack_blogging_app.git'
            }
        }

        stage('Build & Test with Coverage') {
            steps {
                // Runs tests + JaCoCo
                sh 'mvn clean verify'
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
                            echo "Quality Gate status: ${qg.status}"
                            currentBuild.result = 'UNSTABLE'
                        } else {
                            echo "Quality Gate passed"
                        }
                    }
                }
            }
        }
    
        stage('Publish Artifact to Nexus') {
            steps {
                withMaven(
                    maven: 'maven3',
                    jdk: 'jdk17',
                    globalMavenSettingsConfig: 'global-settings'
                ) {
                    sh 'mvn deploy -DskipTests'
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    sh '''
                      export DOCKER_BUILDKIT=0
                      docker build --network devops-net -t ningarajgani/fullstack-blogging-app:${APP_VERSION} .
                      docker tag ningarajgani/fullstack-blogging-app:${APP_VERSION} ningarajgani/fullstack-blogging-app:latest
                    '''
                }
            }
        }

        stage('Deploy Docker Container') {
            steps {
                script {
                    sh '''
                      echo "Stopping old containers if they exist..."
                      docker stop scriblog twitter-app || true
                      docker rm scriblog twitter-app || true

                      echo "Running new container..."
                      docker run -d \
                        --name scriblog \
                        -p 8080:8080 \
                        -e SPRING_DATASOURCE_URL="jdbc:postgresql://db.ziatzkjwpuxlgrzwbalc.supabase.co:6543/postgres?sslmode=require" \
                        -e SPRING_DATASOURCE_USERNAME=postgres.ziatzkjwpuxlgrzwbalc \
                        -e SPRING_DATASOURCE_PASSWORD=NINGU6361@n \
                        -e SUPABASE_URL=https://ziatzkjwpuxlgrzwbalc.supabase.co \
                        -e SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InppYXR6a2p3cHV4bGdyendiYWxjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Njc5NDU0MTIsImV4cCI6MjA4MzUyMTQxMn0.QXgNrpu8NWu48OoDmwTPjAQFgV5u9k0bzULh5YHgY9s \
                        ningarajgani/fullstack-blogging-app:latest
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline completed successfully'
            echo 'SonarQube Dashboard: http://localhost:9000'
        }
        unstable {
            echo 'CI completed with Quality Gate warnings'
        }
        failure {
            echo 'CI Pipeline failed'
        }
        always {
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
    }
}
