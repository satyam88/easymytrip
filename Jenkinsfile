pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }

    tools {
        maven 'maven_3.9.4'
    }

    environment {
        IMAGE_NAME = "satyam88/easymytrip"
        ECR_REGISTRY = "533267238276.dkr.ecr.ap-south-1.amazonaws.com"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def branch = env.GIT_BRANCH
                    if (!branch) {
                        branch = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                        env.GIT_BRANCH = branch
                    }
                }
                checkout scm
            }
        }
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
            when {
                branch 'dev'
            }
            steps {
                script {
                    // Extract the current version from the pom.xml
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def newVersion = version.replace('-SNAPSHOT', '') + "-${env.BUILD_NUMBER}-SNAPSHOT"
                    // Set the new version
                    sh "mvn versions:set -DnewVersion=${newVersion}"
                    echo "Creating War Artifact with version: ${newVersion}"
                    sh 'mvn clean package'
                    echo 'Creating War Artifact Completed'
                }
            }
        }
        stage('Building & Tag Docker Image') {
            steps {
                script {
                    def imageName = "${env.IMAGE_NAME}:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Starting Building Docker Image: ${imageName}"
                    sh "docker build -t ${imageName} ."
                    echo 'Completed Building Docker Image'
                }
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Docker Image Scanning Started'
                // Add Docker image scanning commands here if needed
                echo 'Docker Image Scanning Completed'
            }
        }
        stage('Docker push to Docker Hub') {
            steps {
                script {
                    def imageName = "${env.IMAGE_NAME}:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    withCredentials([usernamePassword(credentialsId: 'DOCKER_HUB_CRED', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        echo "Push Docker Image to DockerHub: In Progress"
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${imageName}"
                        echo "Push Docker Image to DockerHub: Completed"
                    }
                }
            }
        }
        stage('Docker Image Push to Amazon ECR') {
            steps {
                script {
                    def imageName = "${env.IMAGE_NAME}:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    def ecrImageName = "${env.ECR_REGISTRY}/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Tagging the Docker Image: In Progress"
                    sh "docker tag ${imageName} ${ecrImageName}"
                    echo "Tagging the Docker Image: Completed"

                    echo "Push Docker Image to ECR: In Progress"
                    withCredentials([usernamePassword(credentialsId: 'ecr:ap-south-1:ecr-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh "aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin ${env.ECR_REGISTRY}"
                        sh "docker push ${ecrImageName}"
                    }
                    echo "Push Docker Image to ECR: Completed"
                }
            }
        }
        stage ('delete the docker images') {
            steps {
                script {
                    def imageName = "${env.IMAGE_NAME}:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    def ecrImageName = "${env.ECR_REGISTRY}/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Deleting local Docker images: In Progress"
                    sh "docker rmi ${imageName} ${ecrImageName}"
                    echo "Deleting local Docker images: Completed"
                }
            }
        }
    }
}
