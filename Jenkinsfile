pipeline {

    agent { label 'javaJenkinsSlave_TeamA' }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }

    tools {
        maven 'maven_3.9.4'
    }

    stages {
        stage('Code Compilation') {
            steps {
                echo 'Code Compilation is In Progress!'
                sh 'mvn clean compile'
                echo 'Code Compilation is Completed Successfully!'
            }
        }
        stage('Code QA Execution') {
            steps {
                echo 'Junit Test case check in Progress!'
                sh 'mvn clean test'
                echo 'Junit Test case check Completed!'
            }
        }
        stage('Code Package') {
            steps {
                echo 'Creating War Artifact'
                sh 'mvn clean package'
                echo 'Creating War Artifact Completed'
            }
        }
        stage('Building & Tag Docker Image') {
            steps {
                script {
                    def imageName = "satyam88/easymytrip:dev-easymytrip-v.1.${BUILD_NUMBER}"
                    echo "Starting Building Docker Image: ${imageName}"
                    sh "docker build -t ${imageName} ."
                    echo 'Completed Building Docker Image'
                }
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Docker Image Scanning Started'
                sh 'docker --version'
                echo 'Docker Image Scanning Started'
            }
        }
        stage('Docker push to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'docker.io', url: 'https://index.docker.io/v1/', credentials: [$class: 'UsernamePasswordMultiBinding', credentialsId: "${DOCKER_HUB_CRED}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD']]) {
                        echo "Push Docker Image to DockerHub: In Progress"
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${imageName}"
                        echo "Push Docker Image to DockerHub: Completed"
                    }
                }
            }
        }
    }
}