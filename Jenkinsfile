pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    stages {

        stage('Checkout SCM') {
            steps {
                git 'https://github.com/MarcusHoangg/week7_calculator_fx_db.git'
            }
        }

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
                bat 'docker compose up -d'
            }
        }
    }
}