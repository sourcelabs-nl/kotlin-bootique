## Exercise 0: explore the application

Build the project with maven by issues the following command:

```
./mvnw clean install
```

Start the application by running the project using the spring-boot maven plugin:

```
./mvnw spring-boot:run
```

Explore the API using Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

You can add items to the basket via Swagger or using CURL

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
curl -H "Content-Type: application/json" -X POST -d '{"productId":"2","quantity":4}' http://localhost:8080/baskets/1/items
```

We will convert this application to Kotlin.