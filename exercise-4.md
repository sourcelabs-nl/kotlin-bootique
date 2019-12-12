## Exercise 4: Spring 5 Kotlin bean definition DSL

In this exercise we will show a real world example of how to use a DSL written in Kotlin. We will use the Spring 5 Kotlin bean definition DSL as we rewrite our bean configuration in the `BootiqueApplication` class to use this DSL.

### Configuring Spring beans with the Kotlin bean definition DSL

Spring 5 provides a [Kotlin bean definition DSL](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) to define your configuration in a different way. An example from the Spring Documentation:

```kotlin
fun beans() = beans {
    bean<UserHandler>()
    bean<Routes>()
    bean<WebHandler>("webHandler") {
        RouterFunctions.toWebHandler(
            ref<Routes>().router(),
            HandlerStrategies.builder().viewResolver(ref()).build()
        )
    }
    bean("messageSource") {
        ReloadableResourceBundleMessageSource().apply {
            setBasename("messages")
            setDefaultEncoding("UTF-8")
        }
    }
    bean {
        val prefix = "classpath:/templates/"
        val suffix = ".mustache"
        val loader = MustacheResourceTemplateLoader(prefix, suffix)
        MustacheViewResolver(Mustache.compiler().withLoader(loader)).apply {
            setPrefix(prefix)
            setSuffix(suffix)
        }
    }
    profile("foo") {
        bean<Foo>()
    }
}
```
Lets migrate the bean definition in the `BootiqueApplication` to the Kotlin bean definition DSL. We can do that by adding a Kotlin function that returns an instance of a `BeanDefinitionDsl`.
                                                                          
```kotlin
fun beans(): BeanDefinitionDsl = beans {
    bean<T> { 
        ...instantiation
    }
}
```

**Exercise**: add the `beans()` function in the `BootiqueApplication` class.

Now we included the `beans()` function we can migrate the existing bean definition.

**Exercise**: add the `@Bean fun api(): Docket` to the bean DSL and remove the `@Bean fun api(): Docket` function.

You are now done with the conversion to the DSL.

<details>
<summary>Suggested solution:</summary>

```kotlin
fun beans() = beans {
    bean<Docket> {
        Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bootique.bootique"))
                .build()
    }
}
```
</details>

### Spring Boot 2 and Kotlin

Spring Boot 2 provides a few convenient Kotlin extensions. There is an extension function available which allows you to easily use the Kotlin bean definition DSL in the `SpringApplication` runner, see the snippet below:

```kotlin
companion object {
    @JvmStatic
    fun main(args: Array<String>) {
        runApplication<BootiqueApplication>(*args) {
            addInitializers(...add BeanDefinitionDsl instances here...)
        }
    }
}
```

**Exercise**: Replace the existing main implementation by the `runApplication` extension and include the `BeanDefinitionDsl` in the `addInitializers(...)`.

<details>
<summary>Suggested solution:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication {

    /**
     * Swagger2 configuration.
     */
    fun beans() = beans {
        bean<Docket> {
            Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.bootique.bootique"))
                    .build()
        }
    }

    companion object {
        /**
         * Runs the Spring boot application.
         */
         @JvmStatic
        fun main(args: Array<String>) {
            runApplication<BootiqueApplication>(*args) {
                addInitializers(beans()) // Wont compile
            }
        }
    }
}
```
</details>
<br>

We should simplify this code even further. In Kotlin we dont need a class/companion object to run the application, we can move the main function to a top-level function. We can also inline the `beans()` function inside runApplication. 

**Exercise**: Change the main to a top-level function. 

**Exercise**: Inline the body of `fun beans()` inside of the `runApplication { ... }` block

<details>
<summary>Suggested solution:</summary>

```kotlin
/**
 * Spring boot application with Swagger2 enabled.
 */
@SpringBootApplication
@EnableSwagger2
class BootiqueApplication

fun main(args: Array<String>) {
    runApplication<BootiqueApplication>(*args) {
        addInitializers(
            beans {
                bean<Docket> {
                    Docket(DocumentationType.SWAGGER_2)
                            .select()
                            .apis(RequestHandlerSelectors.basePackage("com.bootique.bootique"))
                            .build()
                }
            }
        )
    }
}
```
</details>
<br>

**Exercise**: Build and run the project using maven and see if everything is still working as expected.

We have now successfully migrate the _old_ configuration to the Kotlin bean definition DSL. 

### Next steps

Your are almost there!
 
Continue with [exercise-5](exercise-5.md):

You can either start fresh by switching to the exercise-5 branch or continue on your current branch.

Switching to the exercise-5 branch can be done using IntelliJ or in your terminal by issuing the following command:

```
git checkout exercise-5
```
