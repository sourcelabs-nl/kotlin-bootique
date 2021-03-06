## Exercise 5: Using Kotlin in your test code

Last but not least, we are going to convert the test code from Java to Kotlin and add a Unit test. As with any type of application, we really, really, really encourage testing. It's just a good idea. We've seen many tutorials and blogs covering interesting features of a language, but using these features for writing tests is generally not covered. The Kotlin-Java interoperability will make this easy for the most part, but there are some things to know about that may help in implementing these tests.

### Write a simple unit test

Let's start out by finally adding a simple unit test. Normally of course we would strive towards sensible (branch) coverage targets, but for now we'll just settle for being able to test one of our components. Let's start by writing a test for the BootiqueController class.

In the `src/main/test` folder the file BootiqueControllerTest should be available. It has no contents yet, this is left for you to provide.

We are going to be using a mocking framework to mock our dependencies. Spring Boot bundles Mockito but for Kotlin applications there are other alternatives like MockK/SpringMockK. Since the Java version was using Mockito, lets use that for now. First thing to do now, is to add the MockitoExtension to the test class. This would look something like the listing below.

```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
```

**Exercise**: Add Mockito to the BootiqueControllerTest class.

We are now going to define the unit we are testing and the mocks required for this test. In Java, using mockito you could end up with something similar to the listing below.

```java
@InjectMocks
private BootiqueController bootiqueController;
@Mock
private BasketRepository mockBasketRepository;
@Mock
private ProductRepository mockProductRepository;
```

**Exercise**: Without just converting (because that is almost like cheating ;), define the same thing in Kotlin.

<details>
<summary>Possible solution</summary>

This is something interesting. Kotlin has a well-defined typesystem that by default does not allow undefined values or variables. This means we need to work around the fact that we cannot initialize the tested class and mocks at compile time -- Mockito provides the mocks and initializes the test class at runtime. Luckily, we have options.

If you copied and pasted the Java code listed above, the conversion would have resulted in something like the listing below. This will work, but it forces you to define the property as nullable `BootiqueController?` when it actually shouldn't be null due to Mockito's magic. Additionally, you'd have to assign a default value of `null` to it, which isn't too pretty.

```kotlin
@InjectMocks
private val bootiqueController: BootiqueController? = null
@Mock
private val mockBasketRepository: BasketRepository? = null
@Mock
private val mockProductRepository: ProductRepository? = null
```

Another side-effect of this type of declaration is on the use-site. When calling a function like `bootiqueController.products()` you will get an error; because `bootiqueController` can be null. You will have to use the null-safe assert `bootiqueController!!.products()` or the null-safe operator `bootiqueController?.products()` when calling the function. If only there was a better way...

And there is! Kotlin defines another way to do this, leveraging the lateinit keyword. This keyword can also be used for property/field injection at runtime (although in most cases it makes much more sense to prefer constructor or setter injection over field injection, elminating the need for this approach).

In case you are using lateinit you logically have to specify the target type for the variable, as this can not be inferred.

```kotlin
@InjectMocks
private lateinit var bootiqueController: BootiqueController
@Mock
private lateinit var mockBasketRepository: BasketRepository
@Mock
private lateinit var mockProductRepository: ProductRepository
```
</details>
<br>

Now lets write a simple test for the `getBasket()` operation. To do this, we'd have to use the basketRepository mock, and instruct it to behave in a certain way. This is the test you could possibly write in java:

```java
@Test
public void testRetrieveBasket() {
    final String basketId = "BasketId";
    final Basket basket = new Basket(); 
    when(mockBasketRepository.getBasketById(basketId)).thenReturn(basket);
    assertThat(bootiqueController.getBasket(basketId)).isEqualTo(basket);
}
```

**Exercise**: Create the Kotlin equivalent. See the imports that go with the snippet above listed here.

```
import org.assertj.core.api.Assertions.assertThat
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
```

<details>
<summary>Suggested solution</summary>

The code is not that different from the Java, but there's catch! In Kotlin [`when`](https://kotlinlang.org/docs/reference/grammar.html#when) is a keyword!
Luckily, this was anticipated so in Kotlin we can still use function calls with backticks 
around them. Besides this, it's all the same.

Another interesting possibility is that Kotlin allows you to use whitespace in function names,
as long as you use backticks around them. This allows you to write more expressive test method
names.

```kotlin
@Test
fun `test retrieving basket functionality`() {
    val basketId = "BasketId"
    val basket = Basket()
    `when`(mockBasketRepository.getBasketById(basketId)).thenReturn(basket)
    assertThat(bootiqueController.getBasket(basketId)).isEqualTo(basket)
}
```

</details>
<br>

**Tip** If you find you are getting stuck writing the tests in Kotlin: Write the test in java and convert/copy it into a kotlin file. The conversion will be automatic (via a prompt) and can help you to figure out how to write some of the code in Kotlin. 

If the backticks around `when` give you a headache too you can write a helper function that encapsulates 
this. The definition for Mockito.when is the following:

```java
public static <T> OngoingStubbing<T> when(T methodCall) {
    return MOCKITO_CORE.when(methodCall);
}
``` 

It is a convenient static function, so we can write a Kotlin function that wraps this and substitutes the definition of `when` with `whenever`.

**Exercise**: Implement this function in Kotlin and replace the usage of `when` from the test code.

<details>
<summary>Suggested solution</summary>

Here's the Kotlin code for that, you could add it to the test sources for convenient usage.

```kotlin
fun <T> whenever(methodCall: T): OngoingStubbing<T> {
    return Mockito.`when`(methodCall) // Delegate to escaped when
}

// Usage
whenever(mockBasketRepository.getBasketById(basketId)).thenReturn(basket)
```
</details>

You can also add a useful library to your codebase named [mockito-kotlin](https://github.com/nhaarman/mockito-kotlin) library which enables you to use mockito `when()` as `whenever()` but also is able to handle scenarios where non nullable arguments need to be mocked. It just adds some simple syntactic sugar to Mockito anywhere it makes sense to do so to improve the way Mockito and Kotlin integrate.

**Exercise**: Add mockito-kotlin dependency to your pom.xml and replace the whenever function with the one from the library (import org.mockito.kotlin.whenever).

```xml
<dependency>
    <groupId>org.mockito.kotlin</groupId>
    <artifactId>mockito-kotlin</artifactId>
    <version>3.2.0</version>
</dependency>
```


### Write an application test

Spring provides multiple different ways of testing your application code. For example, you could test you web layer in isolation by using @WebMvcTest. But you can also bootstrap your entire Spring Boot application using a @SpringBootTest. 
In this exercise we are going to test our Spring Boot application using @SpringBootTest by calling an endpoint from our test case, so we'll be modifying the test for this. We are first going to tell Spring Boot to start a server (on a random port) and will wire in a TestRestTemplate to call the service.

First, we'll need to configure the web environment to test against. Modify the `@SpringBootTest` annotation like below.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

Secondly, wire in an instance of the `TestRestTemplate` that will be provided to you by spring
when activating the web environment. It will be provided with host and port preconfigured.

```java
@Autowired
private TestRestTemplate testRestTemplate;
```

**Exercise**: Adapt the logic above to Kotlin for use in the `BootiqueApplicationTests`. Run the test so the context should load, injection of the `TestRestTemplate` should succeed.

<details>
<summary>Suggested solution</summary>

Here's the Kotlin implementation for this:

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootiqueApplicationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate
    
} 
```
</details>

Let's define the test. Implement the body of this function, by using the `TestRestTemplate` to call the service. As we saw with the `when` function a while ago, using backticks allows you to specify method names that may clash with Kotlin keywords. You can also include whitespace in the name of the method -- meaning you can have expressive test names, which can be very helpful when testing.

```kotlin
@Test
fun `get products endpoint should return a list of products`() {}
```

The `/products` endpoint returns a list of products. In order to employ automatic conversion to List<Product> we can use a class called `ParameterizedTypeReference<T>` which will use a typed return value for the template. Consider the following call which (thanks to the `ParameterizedTypeReference<List<Product>`) will return a `List<Product>` as the body:

```kotlin
var response: ResponseEntity<List<Product>> = testRestTemplate.exchange("/products", HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<Product>>() {})
val products = response.body!! 
// todo
```

The response body will be of type `List<Product>` thanks to the usage of the `ParameterizedTypeReference` which helps Spring work out the collection generic type for calls returning collections. This way the `exchange` method will return a `ResponseBody<List<Product>>`. It is a bit verbose though, and requires the use of an anonymous inner class, which in Kotlin is defined using `object : ParameterizedTypeReference<List<Product>>() {}`. This class does not define any abstract method so we can just provide an empty body, but we would have to provide it in every test method.

**Exercise**: Create the test method and add the call listed above to it. Also add an assert to check if the first item in the list has a title with value `"iPhone XX"`. Run the test, it should run properly and succeed (provided you built it right) :)

<details>
<summary>Suggested solution</summary>

The resulting test could look something like this:

```kotlin
@Test
fun `get products endpoint should return a list of products`() {
    val response: ResponseEntity<List<Product>> = testRestTemplate.exchange("/products", HttpMethod.GET, HttpEntity.EMPTY, object : ParameterizedTypeReference<List<Product>>() {})
    assertThat(response.statusCode.value()).isEqualTo(200)

    val products = response.body!!
    assertThat(products.size).isEqualTo(4)
    assertThat(products.first().title).isEqualTo("iPhone XX")
}
```

</details>

As a final exercise, let's leverage some interesting features Kotlin has to offer, extension functions and reified generics, to shorten the TestRestTemplate call.

Spring provides out-of-the-box extension for this but we need to explicitly import such extensions: Futhermore, org.springframework.boot.test.web.client.exchange 

By using the exchange extension function and leveraging reified generics in Kotlin (which is out of scope for this workshop but we will explain it here anyway) we can write the code in an even more concise way. When leveraging reified generics, we can omit the therefore we can omit the `object : ParameterizedTypeReference<List<Product>>() {}` parameter passed in the exchange function. It allows us to either specify the expected type in the variable declaration or to add it to the function call (`testRestTemplate.exchange<List<Product>>`). If you look at the Spring source code snippet below you will see how the exchange function uses the reified keyword:

```kotlin
public inline fun <reified T : kotlin.Any> org.springframework.boot.test.web.client.TestRestTemplate.exchange(...)
```

Because of this the compiler is still able to determine the inferred type for our response variable. Allowing us to write:

```kotlin
val response = testRestTemplate.exchange<List<Product>>("/products", HttpMethod.GET, HttpEntity.EMPTY)
```
or

```kotlin
val response: ResponseEntity<List<Product>> = testRestTemplate.exchange("/products", HttpMethod.GET, HttpEntity.EMPTY)
```

**Exercise**: Try to adjust the testRestTemplate.exchange call to use the extension.

<details>
<summary>Suggested solution</summary>

```kotlin
import org.springframework.boot.test.web.client.exchange

val response = testRestTemplate.exchange<List<Product>>("/products", HttpMethod.GET, HttpEntity.EMPTY)
```

</details> 

As a final exercise we can also test a post, to the products endpoint for example. This shows off the ability Kotlin has to interpolate (multiline) strings. We are going to be testing the endpoint that adds an article to the basket. Take the test below. Notice the multiline string, declared with `"""`. As with any string in Kotlin, we can use string interpolation to set values in the string directly. These are declared as `$productId` and `$quantity`. Anything that's accessible from the scope of the method can be used in String interpolation. You could also call methods, such as `${productId.toUpperCase()}` for instance.

Spring provides convenient extensions for the TestRestTemplate like the postForEntity extension (which is not being used in the example below).

```kotlin
@Test
fun `add product to basket results in updated basket`() {
    val productId = "1"
    val quantity = 2
    
    val body =
    """
    {
        "productId":"todo",
        "quantity": todo
    }
    """
    
    val httpEntity = HttpEntity(body)
    val response = testRestTemplate.postForEntity("/baskets/1/items", httpEntity)

    assertThat(response.statusCode.value()).isEqualTo(200)
}
```

**Exercise**: Try to get the example above working again using the postForEntity extension. Add the variables to the multi-line string. Add the correct content-type header to the HttpEntity. Try to solve this using the apply function!

```kotlin
val headers = HttpHeaders()
headers.contentType = MediaType.APPLICATION_JSON
val entity = HttpEntity(body, headers)
```

<details>
<summary>Suggested solution</summary>
    
```kotlin
import org.springframework.boot.test.web.client.postForEntity

val body =
"""
{
    "productId":"$productId",
    "quantity": $quantity
}
"""

val httpEntity = HttpEntity(body, HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON })
val response = testRestTemplate.postForEntity<Basket>("/baskets/1/items", httpEntity)

```
</details>

## That's it for now ##

That's it, you've done it!

Of course, we are well aware that these tests are somewhat representative of the real-world tests you'll be building, but lack refinement. We hope this will give you the insights you'll need to be able to write some solid tests in Kotlin and at the same time leverage the language features to reduce the volume of code you'll need to write to achieve this.

For the final implementation of this service including the tests above in Kotlin, checkout the [`final` solution branch](https://github.com/sourcelabs-nl/kotlin-bootique-exercises/tree/final).

## Thank you for participating! ##