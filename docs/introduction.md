## Introduction

This tutorial assumes that you have some basic knowledge about Java 8, Maven and Spring Boot.

You need to have Java 8+ and IntelliJ installed.


### Build the application

Build the project with maven by issues the following command:

```
./mvnw clean install
```

### Run the application

Start the application by running the project using the spring-boot maven plugin:

```
./mvnw spring-boot:run
```

### Swagger

Explore the API using Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

You can add items to the basket via Swagger or using CURL

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
curl -H "Content-Type: application/json" -X POST -d '{"productId":"2","quantity":4}' http://localhost:8080/baskets/1/items
```

### Next steps

Make sure the application is working before start converting this application to Kotlin. 

This workshop consist of several exercises that guide you through the process. Each of the exercises are in an seperate git branch. 

By checking out a new exercise you will start of with a working implementation of the previous exercise.

You can start your journey by switching to the exercise-1 branch either by using IntelliJ or issue the following command in your terminal:

```
git checkout exercise-1
```

Enjoy the ride!