## Introduction

The Kotlin Bootique application is a very minimalistic implementation of an API for a Webshop. It provides the following endpoints:

- /products: retrieving product and price information
- /baskets/{id}: retrieving an overview of a shopping basket
- /baskets/{id}/items: being able to add order item to your basket

In this tutorial the Java version of this application will be converted to Kotlin. 

### Prerequisites

This tutorial assumes that you have some basic knowledge about Java 8, Maven and Spring Boot (Web).

Install JDK8+ and IntelliJ (Ultimate Edition, trail is also fine).


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


You can list all the products using your browser, Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -X GET http://localhost:8080/products
```

You can view the content of your basket using your browser, Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -X GET http://localhost:8080/baskets/1
```

You can add items to the basket using Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
curl -H "Content-Type: application/json" -X POST -d '{"productId":"2","quantity":4}' http://localhost:8080/baskets/1/items
```

### Next steps

We will convert this application to Kotlin. You can start your journey with [exercise-1.md](./exercises/exercise-1.md) now!