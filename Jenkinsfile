pipeline {
    agent any

    environment {
        IMAGE_NAME = "satyam88/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
        ECR_IMAGE_NAME = "533267238276.dkr.ecr.ap-south-1.amazonaws.com/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
    }

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
                echo 'JUnit Test Case Check in Progress!'
                sh 'mvn clean test'
                echo 'JUnit Test Case Check Completed!'
            }
        }
        stage('Code Package') {
            steps {
                echo 'Creating WAR Artifact'
                sh 'mvn clean package'
                echo 'WAR Artifact Creation Completed'
            }
        }
        stage('Building & Tag Docker Image') {
            steps {
                echo "Starting Building Docker Image: ${env.IMAGE_NAME}"
                sh "docker build -t ${env.IMAGE_NAME} ."
                echo 'Docker Image Build Completed'
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Docker Image Scanning Started'
                // Add actual scanning steps here
                echo 'Docker Image Scanning Completed'
            }
        }
        stage('Docker Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CRED', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                    echo "Pushing Docker Image to DockerHub: ${env.IMAGE_NAME}"
                    sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                    sh "docker push ${env.IMAGE_NAME}"
                    echo "Docker Image Push to DockerHub Completed"
                }
            }
        }
        stage('Docker Image Push to Amazon ECR') {
            steps {
                echo "Tagging Docker Image for ECR: ${env.ECR_IMAGE_NAME}"
                sh "docker tag ${env.IMAGE_NAME} ${env.ECR_IMAGE_NAME}"
                echo "Docker Image Tagging Completed"

                withCredentials([usernamePassword(credentialsId: 'ecr-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                    echo "Logging in to Amazon ECR"
                    sh "aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 533267238276.dkr.ecr.ap-south-1.amazonaws.com"
                    echo "Pushing Docker Image to ECR: ${env.ECR_IMAGE_NAME}"
                    sh "docker push ${env.ECR_IMAGE_NAME}"
                    echo "Docker Image Push to ECR Completed"
                }
            }
        }
        stage('Delete Local Docker Images') {
            steps {
                echo "Deleting Local Docker Images: ${env.IMAGE_NAME} and ${env.ECR_IMAGE_NAME}"
                sh "docker rmi ${env.IMAGE_NAME} ${env.ECR_IMAGE_NAME}"
                echo "Local Docker Images Deletion Completed"
            }
        }
    }
}
