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

        stage('Deploy from Nexus (Docker)') {
            steps {
                script {
                    sh '''
                      docker build --network devops-net -t twitter-app:${BUILD_NUMBER} .
                      docker rm -f twitter-app || true
                      docker run -d \
                        --name twitter-app \
                        --network devops-net \
                        -p 8080:8080 \
                        twitter-app:${BUILD_NUMBER}
                    '''
                }
            }
        }

        stage('Package (Skip Tests)') {
            steps {
                sh 'mvn package -DskipTests'
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
