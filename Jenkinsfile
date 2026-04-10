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
                sh '''
                    mvn clean compile 2>&1 | tee build-log.txt
                    if grep -q "ERROR" build-log.txt; then
                        echo "BUILD_STATUS=FAILED" > status.txt
                        echo "BUILD_ERRORS<<EOF" >> status.txt
                        grep "ERROR" build-log.txt >> status.txt
                        echo "EOF" >> status.txt
                    else
                        echo "BUILD_STATUS=PASSED" > status.txt
                        echo "BUILD_ERRORS=None" >> status.txt
                    fi
                '''
            }
        }
        stage('Test') {
            steps {
                sh '''
                    mvn test 2>&1 | tee test-log.txt
                    if grep -qE "FAILED|ERROR" test-log.txt; then
                        echo "TEST_STATUS=FAILED" >> status.txt
                        grep -E "FAILED|ERROR" test-log.txt >> status.txt
                    else
                        echo "TEST_STATUS=PASSED" >> status.txt
                    fi
                '''
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
                sh 'docker cp status.txt maven-project-container:/app/status.txt || true'
                sh 'docker run -d -p 9090:8080 --name maven-project-container maven-project'
                sh 'docker cp status.txt maven-project-container:/app/status.txt'
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

//    pipeline {
//     agent any

//     tools {
//         maven 'Maven'
//     }

//     stages {
//         stage('Clone Code') {
//             steps {
//                 git branch: 'main',
//                     url: 'https://github.com/SAKETH-V/Maven-Project.git'
//             }
//         }

//         stage('Build') {
//             steps {
//                 sh 'mvn clean compile'
//             }
//         }

//         stage('Test') {
//             steps {
//                 sh 'mvn test'
//             }
//         }

//         stage('Package') {
//             steps {
//                 sh 'mvn package -DskipTests'
//                 echo 'JAR created in target/ folder'
//             }
//         }

//         stage('Docker Build') {
//             steps {
//                 sh 'docker build -t maven-project .'
//             }
//         }

//         stage('Docker Run') {
//             steps {
//                 sh 'docker stop maven-project-container || true'
//                 sh 'docker rm maven-project-container || true'
//                 sh 'docker run -d -p 9090:8080 --name maven-project-container maven-project'
//             }
//         }
//     }

//     post {
//         success {
//             echo '✅ Build & Deployment Successful! App running at http://localhost:9090'
//         }
//         failure {
//             echo '❌ Build Failed. Check Console Output.'
//         }
//     }
// }
