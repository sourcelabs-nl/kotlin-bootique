## Kotlin Bootique Workshop

### Introduction

Welcome to the Kotlin Bootique Workshop. The Kotlin Bootique application provides a very minimalistic implementation of a RESTful API for building a Webshop. The application consists of the following endpoints:

| Uri                   | HttpMethod    | Description                                       |
| --------------------- | :-----------: | ------------------------------------------------- |
| _/products_           | GET           | retrieve product and price information            |
| _/baskets/{id}_       | GET           | retrieve a shopping basket                        |
| _/baskets/{id}/items_ | POST          | adding an item to a basket                        |

Throughout this workshop you are going to convert this Spring Boot based application written in Java to the Kotlin equivalent. You will be guided through some of the challenges you as a developer will be facing while migrating existing applications to Kotlin.

If you are able to successfully complete this workshop, you should have enough knowledge to start using Kotlin in any of your (existing) Java projects.

### Prerequisites

This tutorial assumes that you at least have some basic knowledge about Java 8, Maven, Git and Spring Boot.

#### Git

Download and install git from [https://git-scm.com/downloads](https://git-scm.com/downloads)

#### Java

Make sure you have at least JDK 8 or 11 installed. You can check which version is installed by executing in your terminal:

```
java -version
```

Should output something similar like:

```bash
java -version
openjdk version "11.0.5" 2019-10-15
OpenJDK Runtime Environment AdoptOpenJDK (build 11.0.5+10)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 11.0.5+10, mixed mode)
```

Java can be downloaded from the AdaptOpenJDK website: [https://adoptopenjdk.net/](https://adoptopenjdk.net/). Or, if you really need to, from the Oracle website: [http://www.oracle.com/technetwork/java/javase/downloads/index.html](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

#### IDE - IntelliJ

In this tutorial we assume that you will be using IntelliJ because of it`s excellent Kotlin support! That makes sense considering Kotlin was invented and created by JetBrains, the creator of IntelliJ.

IntelliJ Ultimate Edition is preferred but IntelliJ Community will also work. IntelliJ can be downloaded from: [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/).

#### Sources

The Kotlin Bootique exercises can be found in a separate git repository. Please clone the source to your local machine from github:

```
git clone https://github.com/sourcelabs-nl/kotlin-bootique-exercises.git
```

Keep this documentation open in your browser while completing the exercises.

### Build the application

Open the project in IntelliJ ( File > Open... ) or navigate with your favorite terminal application to the location where you just cloned the project. 

You can build the project using maven by executing the following command: 

```
./mvnw clean install
```

_mvnw_ is no typo, this makes sure you are using the maven wrapper file. If you experience issues with you existing maven settings file you can use the provided settings.xml inside the project. Or temporarily renamed your settings.xml to settings.tmp.

```
./mvnw -s settings.xml clean install
```

### Run the application

You can launch the Kotlin Bootique application by running the project using the spring-boot maven plugin. Execute the following maven command:

```
./mvnw spring-boot:run
```

### Exploring the API

The Kotlin Bootique application exposes a Swagger endpoint that allows you to explore the API: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Alternatively you can also execute all the request against the API using curl or Postman (or a browser for all the GET requests). 

#### Products

You can list all the products using your browser, Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -X GET http://localhost:8080/products
```

#### Basket

You can view the content of your basket using your browser, Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -X GET http://localhost:8080/baskets/1
```

#### Adding items

You can add items to the basket using Swagger or curl

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
curl -H "Content-Type: application/json" -X POST -d '{"productId":"2","quantity":4}' http://localhost:8080/baskets/1/items
```

### Next steps

This workshop consist of several exercises that will guide you through the process of converting the Java code to Kotlin. Each of the exercises are in the kotlin-bootique-exercises project on an separate git branch. 

By checking out each exercise branch, you will start of with a working implementation of the previous exercise.

The lines marked with: **Exercise** are the task that need to be performed!

You can start your journey now by switching to the exercise-1 branch, either by using IntelliJ or, by issue the following command in your terminal:

```
git checkout exercise-1
```

Please keep this documentation open in your browser while completing the exercises.

The documentation for the first exercise can be found here: [exercise-1.md](./exercise-1.md)

Enjoy the ride!
