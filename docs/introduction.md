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

We will convert this application to Kotlin. You can start your journey with [exercise-1.md](./exercises/exercise-1.md) now!