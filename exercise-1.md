## Exercise 1: prepare your project for Kotlin

In this exercise we will modify the setup of an existing Spring Boot Java project to be able to start using Kotlin. 

This project uses maven for building the application, for gradle projects the same concepts apply.

### Add Kotlin to your maven project

Prepare the maven pom.xml for Kotlin.
 
**Exercise**: add a maven property that defines the Kotlin version to the existing properties.

<details>
<summary>Snippet</summary>

```xml
<properties>
    ...
    <kotlin.version>1.2.0</kotlin.version>
</properties>
```
</details>
<br>

**Exercise**: Add [Kotlin dependencies](https://kotlinlang.org/docs/reference/using-maven.html) to the pom.xml and use the jre8 version of the [kotlin stdlib](https://kotlinlang.org/api/latest/jvm/stdlib/index.html). 

<details>
<summary>Snippet</summary>

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
</details>

### Add the Kotlin maven plugin

Just like with Java, you need to configure a [kotlin maven (compiler) plugin](https://kotlinlang.org/docs/reference/using-maven.html) for the compilation of Kotlin files. 

**Exercise**: Add the kotlin-maven-plugin to the pom.xml using the snippet below!

<details>
<summary>Snippet</summary>

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
</details>
<br>

Your project is now ready for some kotlin code! Rebuild the project using maven by executing the following command:

```xml
./mvnw clean verify
```

### Convert Java to Kotlin

Lets convert some Java code to Kotlin, start with the BootiqueApplication.java file. You can try to do this manually but this can easily be done using IntelliJ via the menu option Code > Convert Java File to Kotlin File.

Build the project with maven (./mvnw clean verify).

You should see an error in the tests. The BootiqueApplicationTests try to bootstrap the Spring Boot application but fails with the following error:
```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Configuration class 'BootiqueApplication' may not be final. 
Remove the final modifier to continue.
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the classes are final by default, this causes an issue when using Spring (Boot). Spring needs to be able to subclass (Proxy) you configuration classes and components. In kotlin we can mark a class open so it can be inherited by other classes. 

**Exercise**: Add the open keyword to the BootiqueApplication class definition.

<details>
<summary>Snippet</summary>

```kotlin
open class BootiqueApplication
```
</details>
<br>
Build the project with maven (./mvnw clean verify), is it working?

```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Bean method 'api' must not be private or final; 
change the method's modifiers to continue
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the methods are also final by default. Since Spring wants to proxy the methods you need to declare them open as well.

While this might be fine in our case with just one method, consider an application with multiple configuration classes and/or bean definitions. We can use an plugin for the Kotlin maven plugin to ensure all Spring related classes and methods are made open by default.

**Exercise**: Enable the [spring plugin for the kotlin-maven-plugin](https://kotlinlang.org/docs/reference/compiler-plugins.html)

<details>
<summary>Snippet</summary>

Add the following configuration to the kotlin-maven-plugin, just after: `<version>${kotlin.version}</version>`

```xml
...
<version>${kotlin.version}</version>
<configuration>
    <compilerPlugins>
        <plugin>spring</plugin>
    </compilerPlugins>
    <jvmTarget>1.8</jvmTarget>
</configuration>
<executions>
...
```
</details>
<br>

Build the project with maven (./mvnw clean verify), is it working now? 

**Exercise**: You can now also remove the _open_ keyword from the BootiqueApplication class definition if it really bothers you.

### Next steps

Continue with [exercise-2](exercise-2.md):

```
git checkout exercise-2
```

