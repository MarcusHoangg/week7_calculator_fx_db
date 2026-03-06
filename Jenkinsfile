pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    stages {

        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t calculator-app .'
            }
        }

        stage('Run Docker Compose') {
            steps {
                bat 'docker compose down --remove-orphans'
                bat 'docker compose up -d --build'
                bat 'docker ps'
            }
        }
    }
}