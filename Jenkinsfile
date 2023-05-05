pipeline {
    agent any

    tools {
        gradle "Default"
    }

    environment {
        PRIVATE_REPO_URL = "$NEXUS_URL"
        PRIVATE_REPO_CRED = credentials('LDAP-Account')
    }

    stages {
        stage('Clone') {
            steps {
                checkout scmGit(branches: [[name: '*/main']], browser: [$class: 'GiteaBrowser', repoUrl: ''], extensions: [], userRemoteConfigs: [[credentialsId: 'LDAP-Account', url: '$GITEA_URL/toy/devtools.git']])
                script {
                    env.TAG_NAME = sh(script: 'git tag --points-at HEAD | sort -r | head -1', returnStdout: true).trim()
                    env.RELEASE_VERSION = "$TAG_NAME"
                }

                echo "Currnt TAG is $TAG_NAME"
            }
        }
        stage('Test') {
            steps {
                sh './gradlew clean test'
                junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Publish') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'LDAP-Account', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh './gradlew publish'
                }
            }
        }

        stage('Dockerize') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'LDAP-Account', passwordVariable: 'DOCKER_REGISTRY_PASSWORD', usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                    sh './gradlew -DsendCredentialsOverHttp=true jib'
                }
            }
        }

        stage('Release') {
            when {tag "v*"}
            steps {
                script {
                    env.NEXUS_URL = "$GITHUB_MAVEN_REPO"
                }
                withCredentials([usernamePassword(credentialsId: 'Github-PackageAccount', passwordVariable: 'NEXUS_PASSWORD', usernameVariable: 'NEXUS_USERNAME')]) {
                    sh './gradlew publish'
                }
            }
        }

        stage('Release-Docker') {
            when {tag "v*"}
            steps {
                script {
                    env.DOCKER_REGISTRY_URL = ""
                }
                withCredentials([usernamePassword(credentialsId: 'DockerHub-Release', passwordVariable: 'DOCKER_REGISTRY_PASSWORD', usernameVariable: 'DOCKER_REGISTRY_USER')]) {
                    sh './gradlew jib'
                }
            }
        }
    }
}
