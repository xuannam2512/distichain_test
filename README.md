# Synchronize Service

## Pre requisites

- Java OpenJDK 11
- Maven >3.5

Check the docker-compose.yml in the project_root/docker to fully start all the requirements of the application.

## Stack

- Spring boot 2.5.4

## Recommended IDE

- Intellij IDEA

## Quick start

- At root folder run `mvn clean install` and then `mvn springboot:run`

- Or at root folder run `docker-compose up --build`

## API documentation

- `http://localhost:8080/synchronize - GET` to check status
- `http://localhost:8080/synchronize - POST` to start synchronize
- `http://localhost:8080/synchronize - DELETE` to stop synchronize
