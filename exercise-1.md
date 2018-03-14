## Exercise 1: preparing a project for Kotlin

In this exercise we will modify the setup of the Kotlin Bootique application to be able to start writing Kotlin code. 

This project uses maven for building the application, for gradle projects the same concepts apply, but configuration is somewhat different.

### Add Kotlin to your maven project

Prepare the maven pom.xml for Kotlin. 
 
**Exercise**: add the `<kotlin.version>` maven property to the pom.xml. We will use this property to define the version of Kotlin dependencies used in this project.


```xml
<properties>
    ...
    <kotlin.version>1.2.30</kotlin.version>
</properties>
```

**Exercise**: Add the following [Kotlin dependencies](https://kotlinlang.org/docs/reference/using-maven.html) to the pom.xml and use the jdk8 version of the [kotlin stdlib](https://kotlinlang.org/api/latest/jvm/stdlib/index.html). 

```xml
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-stdlib-jdk8</artifactId>
    <version>${kotlin.version}</version>
</dependency>
<dependency>
    <groupId>org.jetbrains.kotlin</groupId>
    <artifactId>kotlin-reflect</artifactId>
    <version>${kotlin.version}</version>
</dependency>
```

You project is now ready to use the Kotlin standard library and reflection utilities which are used by frameworks like Jackson.

### Add the Kotlin maven plugin

Just like with Java, you need to configure a maven plugin for the compilation of files, in this case, Kotlin files. The [kotlin maven plugin](https://kotlinlang.org/docs/reference/using-maven.html) handles the compilation of Kotlin files. 

**Exercise**: Add the kotlin-maven-plugin to the pom.xml using the snippet below!

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

Your project is now ready for the compilation of Kotlin files! Since we did not alter any of the source directories in our maven configuration, you can just mix Java and Kotlin files in the folder: _src/main/java_

**Exercise**: Build the project using maven by executing the following maven command `./mvnw clean verify`

The build should be successful!

### Converting Java to Kotlin

Let's convert some Java code to Kotlin, we can start with the BootiqueApplication.java file. You can try to do this manually but this can easily be done using IntelliJ via the menu option: _Code > Convert Java File to Kotlin File_

**Exercise**: Build the project using maven by executing the following maven command `./mvnw clean verify`

You should see an error in the tests. The BootiqueApplicationTests tries to bootstrap the Spring Boot application but fails with the following error:
```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Configuration class 'BootiqueApplication' may not be final. 
Remove the final modifier to continue.
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the classes are final by default, unless specified otherwise using the open keyword. This causes an issue with frameworks like Spring (Boot) that need to be able to subclass (Proxy) your configuration classes and components. Since final classes can't be subclassed your code is now broken. We can mark a class open so it can be extended by other classes. 

**Exercise**: Add the open keyword to the BootiqueApplication class definition.

<details>
<summary>Suggested solution:</summary>

```kotlin
open class BootiqueApplication
```
</details>
<br>

**Exercise**: Build the project using maven by executing the following maven command `./mvnw clean verify`

```
org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: @Bean method 'api' must not be private or final; 
change the method's modifiers to continue
Offending resource: com.bootique.bootique.BootiqueApplication
```

What happened? In Kotlin all the methods are also final by default, unless specified otherwise using the open keyword. Since Spring wants to proxy (implement) the @Bean methods you need to declare them open as well.

While this might be fine in our case with just one @Bean method, consider an (existing) application with multiple configuration classes and/or bean definitions, this will be some work to mark all these functions with open.
 
Fortunately we can use a plugin for the Kotlin maven plugin to ensure all Spring related classes and methods are made open by default.

**Exercise**: Try to enable the [spring plugin for the kotlin-maven-plugin](https://kotlinlang.org/docs/reference/compiler-plugins.html)

<details>
<summary>Suggested solution:</summary>

Add the following configuration to the kotlin-maven-plugin, just after: `<version>${kotlin.version}</version>`

```xml
...
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
...
```
</details>
<br>

**Exercise**: Build the project using maven by executing the following maven command `./mvnw clean verify`

The build should be successful!

### Next steps

Continue with [exercise-2](exercise-2.md). 

You can either start fresh by switching to the exercise-2 branch or continue on your current branch.

Switching to the exercise-2 branch can be done using IntelliJ or in your terminal by issuing the following command:

```
git checkout exercise-2
```
