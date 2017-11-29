## Exercise 1: prepare your project for Kotlin

In this exercise we will modify the setup an existing Spring Boot Java project to be able to start using Kotlin. 

This project uses maven for building the application but when using gradle the same concepts and changes apply.

### Add Kotlin to your maven project

Let prepare your maven pom.xml for Kotlin. Add a maven property that defines the Kotlin version to the existing properties:
```xml
<properties>
    ...
    <kotlin.version>1.2.0</kotlin.version>
</properties>
```

Add the Kotlin dependencies, we will use the Java 8+ version of the stdlib.
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

Just like Java, you need to configure a compiler plugin for the compilation of Kotlin files. 
```xml
<plugin>
    <artifactId>kotlin-maven-plugin</artifactId>
    <groupId>org.jetbrains.kotlin</groupId>
    <version>${kotlin.version}</version>
    <executions>
        <execution>
            <id>compile</id>
            <phase>process-sources</phase>
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
Your project is ready to start writing kotlin! Now rebuild the project using maven by executing the following command:

```xml
./mvnw clean verify
```

Now convert the BootiqueApplication.java file to Kotlin. You can do this in IntelliJ via the menu option Code > Convert Java File to Kotlin File.

Build the project with maven (./mvnw clean verify).

You should see the following error:
```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Configuration class 'BootiqueApplication' may not be final. Remove the final modifier to continue.
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the classes are final by default. So we need to explicitly make the class open, add the open keyword to the class definition.

```kotlin
open class BootiqueApplication
```

Build the project with maven (./mvnw clean verify), is it working?

```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Bean method 'api' must not be private or final; change the method's modifiers to continue
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happend? In Kotlin all the methods are also final by default. Since Spring wants to proxy the methods you need to declare them open as well.

This might be fine in our case where there is just one method, but consider an application with multiple configuration classes and/or bean definitions.

We can use a kotlin compiler plugin for spring application to ensure all Spring related classes and methods are defined open by default.

Add the following configuration to the kotlin-maven-plugin (just after <version>${kotlin.version}</version>):

```xml
<configuration>
    <compilerPlugins>
        <plugin>spring</plugin>
    </compilerPlugins>
    <jvmTarget>1.8</jvmTarget>
</configuration>
```

Build the project with maven (./mvnw clean verify), is it working? Should be fine now! 

You can now also remove the open keyword from the BootiqueApplication class definition if it bothers you.