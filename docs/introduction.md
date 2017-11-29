## Introduction

The Kotlin Bootique application is a very minimalistic implementation of an API for a Webshop. It provides the following endpoints:

- /products: retrieving product and price information
- /baskets/{id}: retrieving an overview of a shopping basket
- /baskets/{id}/items: being able to add order item to your basket

In this tutorial the Java version of this application will be converted to Kotlin. 

### Prerequisites

This tutorial assumes that you have some basic knowledge about Java 8, Maven, Git and Spring Boot (Web).

First, clone this project to your local machine:

```
git clone https://github.com/soudmaijer/kotlin-bootique.git
```

Install JDK8+ and IntelliJ (Ultimate Edition, trail is also fine).


### Build the application

Open the project with IntelliJ or go with your favorite terminal application to the location where you just cloned the project. 

You can build the project using maven by firing the following command:

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

This workshop consist of several exercises that guide you through the process. Each of the exercises are in an separate git branch. 

By checking out a new exercise you will start of with a working implementation of the previous exercise.

You can start your journey by switching to the exercise-1 branch either by using IntelliJ or issue the following command in your terminal:

```
git checkout exercise-1
```

The exercises can be found in the [./exercises](exercises) folder.

Enjoy the ride!