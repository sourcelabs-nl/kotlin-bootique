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
