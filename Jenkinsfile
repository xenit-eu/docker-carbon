pipeline {
    agent any

    stages {
        stage("Clean") {
            steps {
                sh "./gradlew clean -i"
            }
        }
        stage("Build Docker Image") {
            steps {
                sh "./gradlew buildDockerImage"
            }
        }
        stage("Test") {
            steps {
                sh "./gradlew test"
            }
        }
        stage("Publish Docker Image") {
            when {
                anyOf {
                    branch "master*"
                    branch "release*"
                }
            }
            steps {
                sh "./gradlew pushDockerImage"
            }
        }
    }

    post {
        always {
            script {
                junit '**/build/**/TEST-*.xml'
            }
        }
    }
}
