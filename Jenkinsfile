pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
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

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar') {
                    sh '''
                        mvn sonar:sonar \
                        -Dsonar.projectKey=fullstack-blogging-app \
                        -Dsonar.projectName=FullStack-Blogging-App \
                        -Dsonar.java.binaries=target/classes
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: false
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
                sh 'docker build -t fullstack-blogging-app:latest .'
            }
        }
    }

    post {
        success {
            echo 'CI pipeline completed successfully'
            echo 'SonarQube Dashboard: http://localhost:9000'
        }
        failure {
            echo 'CI pipeline failed'
        }
    }
}
