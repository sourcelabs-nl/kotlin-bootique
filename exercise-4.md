## Exercise 4: Spring 5 Kotlin bean definition DSL

In this exercise we will show a real world example of using a DSL written in Kotlin. We will use the Spring 5 Kotlin bean defintion DSL to rewrite the BootiqueApplication.kt class to this DSL.

### Configuring the beans

Spring 5 provides a [Kotlin Beans definition DSL](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) to define your application configuration in a functional way.

We will add this to our existing Spring Boot application. In this exercise the goal is to make the SpringBootApplication class more concise. Later you can decide whether you like it or not :-)

**Exercise**: remove the curly braces from the BootiqueApplication class.

We can define the DSL using a Kotlin function in the BootiqueApplication. Below is an example of a Kotlin function that defines the BeanDefinitionDsl.
                                                                          
```kotlin
fun beans(): BeanDefinitionDsl = beans {
    bean<T> { 
        ...instantiation
    }
}
```

Let migrate the existing Spring configuration to the Kotlin beans definition DSL

**Exercise**: add the beans() function in the BootiqueApplication.kt file.

Now we included the BeanDefinitionDsl we can migrate the existing bean definition.

**Exercise**: add the `@Bean fun api(): Docket` to the beans DSL and remove the `@Bean fun api(): Docket` function.

You are now done!

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
fun beans() = beans {
    bean<Docket> {
        Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
    }
}
```
</details>

### Spring Boot 2 and the Kotlin bean definition DSL

We need to configure the SpringApplication runner to start using the BeanDefinitionDsl. Spring Boot 2 provides some Kotlin extensions to do just that:

```kotlin
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(...add beans defintion dsl here...)
    }
}
```

**Exercise**: Replace the existing main (including companion object) by this Spring Boot extension.

Right now you should have a `BootiqueApplication.kt` file with only a Kotlin class definition and two functions: beans() and main().

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication

/**
 * Swagger2 configuration.
 */
fun beans() = beans {
    bean<Docket> {
        Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
    }
}

/**
 * Runs the Spring boot application.
 */
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(beans())
    }
}
```
</details>
<br>

We could simplify this code even further by in-lining the beans() function inside runApplication.

**Exercise**: move  `beans { ... }` inside of the `runApplication { ... }` block

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication

/**
 * Runs the Spring boot application.
 */
fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        beans {
            bean<Docket> {
                Docket(DocumentationType.SWAGGER_2)
                        .select()
                        .apis(RequestHandlerSelectors.any())
                        .paths(PathSelectors.any())
                        .build()
            }
        }
    }
}
```
</details>
<br>

### Next steps

Your are almost there!
 
Continue with [exercise-5](exercise-5.md):

```
git checkout exercise-5
```