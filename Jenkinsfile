   pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    stages {
        stage('Clone Code') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/SAKETH-V/Maven-Project.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
                echo 'JAR created in target/ folder'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t maven-project .'
            }
        }

        stage('Docker Run') {
            steps {
                sh 'docker stop maven-project-container || true'
                sh 'docker rm maven-project-container || true'
                sh 'docker run -d -p 9090:8080 --name maven-project-container maven-project'
            }
        }
    }

    post {
        success {
            echo '✅ Build & Deployment Successful! App running at http://localhost:9090'
        }
        failure {
            echo '❌ Build Failed. Check Console Output.'
        }
    }
}
