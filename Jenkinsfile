pipeline {
    agent any

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
                    def imageName = "satyam88/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Starting Building Docker Image: ${imageName}"
                    sh "docker build -t ${imageName} ."
                    echo 'Completed Building Docker Image'
                }
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Docker Image Scanning Started'
                echo 'Docker Image Scanning Completed'
            }
        }
        stage('Docker push to Docker Hub') {
            steps {
                script {
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
                    def ecrImageName = "533267238276.dkr.ecr.ap-south-1.amazonaws.com/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Tagging the Docker Image: In Progress"
                    sh "docker tag ${imageName} ${ecrImageName}"
                    echo "Tagging the Docker Image: Completed"

                    echo "Push Docker Image to ECR: In Progress"
                    withCredentials([usernamePassword(credentialsId: 'ecr:ap-south-1:ecr-credentials', usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
                        sh "aws ecr get-login-password --region ap-south-1 | docker login --username AWS --password-stdin 533267238276.dkr.ecr.ap-south-1.amazonaws.com"
                        sh "docker push ${ecrImageName}"
                    }
                    echo "Push Docker Image to ECR: Completed"
                }
            }
        }
        stage ('delete the docker images') {
            steps {
                script {
                    def ecrImageName = "533267238276.dkr.ecr.ap-south-1.amazonaws.com/easymytrip:dev-easymytrip-v.1.${env.BUILD_NUMBER}"
                    echo "Deleting local Docker images: In Progress"
                    sh "docker rmi ${imageName} ${ecrImageName}"
                    echo "Deleting local Docker images: Completed"
                }
            }
        }
    }
}
