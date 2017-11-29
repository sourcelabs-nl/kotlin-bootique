## Exercise 1 - prepare project

Start the application by running the BootiqueApplication.

Explore the API using Swagger: http://localhost:8080/swagger-ui.html

You can add items to the basket via Swagger or using CURL

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
curl -H "Content-Type: application/json" -X POST -d '{"productId":"2","quantity":4}' http://localhost:8080/baskets/1/items
```

We will convert this application to Kotlin.

### Add the Kotlin runtime and dependencies

Configure the maven project to use kotlin. Add a maven property that defines the kotlin version:
```xml
<propties>
    <kotlin.version>1.2.0</kotlin.version>
</propties>
```

Add kotlin dependencies, we will use the Java >8 target runtime
```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jre8</artifactId>
    <version>${kotlin.version}</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>${kotlin.version}</version>
</dependency>
```

### Add the Kotlin maven plugin

Snippet
```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>${kotlin.version}</version>
    <configuration>
        <compilerPlugins>
            <plugin>spring</plugin>
        </compilerPlugins>
        <jvmTarget>1.8</jvmTarget>
    </configuration>
    <executions>
        <execution>
            <id>compile</id>
            <phase>compile</phase>
            <goals>
                <goal>compile</goal>
            </goals>
        </execution>
        <execution>
            <id>test-compile</id>
            <phase>test-compile</phase>
            <goals>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.jetbrains.kotlin</groupId>
            <artifactId>kotlin-maven-allopen</artifactId>
            <version>${kotlin.version}</version>
        </dependency>
    </dependencies>
</plugin>
```
Now your project is ready to use kotlin!