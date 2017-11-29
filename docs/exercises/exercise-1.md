## Exercise 1: prepare your project for Kotlin

### Add Kotlin to your maven project

Configure your maven pom.xml to use Kotlin. For easy of configuration we add a maven property that defines the Kotlin version:
```xml
<properties>
    ...
    <kotlin.version>1.2.0</kotlin.version>
</properties>
```

Add kotlin dependencies, we will use the Java 8+ target runtime
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