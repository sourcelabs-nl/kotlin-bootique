## Exercise 5: Using Kotlin in your test code

Last but not least, we are going to convert the test code from Java to Kotlin and add a Unit test. As with any type of application, we really, really, really encourage testing. It's just a good idea. We've seen many tutorials and blogs covering interesting features of a language, but using these features for writing tests is generally not covered. The Kotlin-Java interoperability will make this easy for the most part, but there are some things to know about that may help in implementing these tests.

### Write a simple unit test

Let's start out by finally adding a simple unit test. Normally of course we would strive towards sensible (branch) coverage targets, but for now we'll just settle for being able to test one of our components. Let's start by writing a test for the BootiqueController class.

**Exercise** Implement a test class for the BootiqueController

In the `src/main/test` folder the file BootiqueControllerTest should be available. It has no contents yet, this is left for you to provide.

We are going to be using a mocking framework to mock out our dependencies. Spring test conveniently bundles Mockito so let's use that. First thing to do now, is to define the MockitoRunner as the testrunner for your unit test. Add it now.

In java you would do something like the listing below. Define the test class and add the runner declaration to it.

```java
@RunWith(MockitoJUnitRunner.class)
```

<details>
<summary>Suggested solution</summary>

In Kotlin we can refer to classes with the double colon notation: `MockitoJUnitRunner::class`

```kotlin
@RunWith(MockitoJUnitRunner::class)
class BootiqueControllerTest
```
</details>
<br>

**Exercise** Define the instance we are testing and the mocks Mockito should inject

We are now going to define the unit we are testing and the mocks required by this unitto function. 

In Java, you could end up with something similar to the listing below.

```java
@InjectMocks
private BootiqueController bootiqueController;

@Mock
private ProductRepository mockProductRepository;

@Mock
private ProductRepository mockBasketRepository;
```

Without just converting (because that is almost like cheating ;), define the same thing in Kotlin.

<details>
<summary>Possible solution</summary>

This is something interesting. Kotlin has a well-defined typesystem that by default does not allow undefined values or variables. This means we need to work around the fact that we cannot initialize the tested class and mocks at compile time -- Mockito provides the mocks and initializes the test class at runtime. Luckily, we have options.

If you copied and pasted the Java code listed above, the conversion would have resulted in something like the listing below. This will work, but it forces you to define the property as nullable `BootiqueController?` when it actually shouldn't be null due to Mockito's magic. Additionally, you'd have to assign a default value of `null` to it, which isn't too pretty.

```kotlin
@InjectMocks
private val bootiqueController: BootiqueController? = null

@Mock
private val mockProductRepository: ProductRepository? = null

@Mock
private val mockBasketRepository: ProductRepository? = null
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

**Exercise** Write a simple test for the `getBasket()` operation.

To do this, we'd have to use the basketRepository mock, and instruct it to behave in a certain way. This is the test you could possibly write in java:

```java
@Test
public void testRetrieveBasket() {
    final String basketId = "BasketId";
    final Basket basket = new Basket();
    
    when(mockBasketRepository.getBasketById(basketId)).thenReturn(basket);
    
    assertThat(bootiqueController.getBasket(basketId)).isEqualTo(basket);
    
    verify(mockBasketRepository).getBasketById(basketId);
}
```

Now create the Kotlin equivalent. See the imports that go with the snippet above listed here.

```
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
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
    
    verify(mockBasketRepository).getBasketById(basketId)
}
```

</details>
<br>

**Pro-tip if you find you are getting stuck writing the tests in Kotlin: Write the test in java and convert/copy it into a kotlin file. The conversion will be automatic (via a prompt) and can help you to figure out how to write some of the code in Kotlin.** 

**Exercise** Remove the need for backticks around the when

If the backticks around `when` give you a headache too you can write a helper function that encapsulates 
this. The definition for Mockito.when is the following:

```java
public static <T> OngoingStubbing<T> when(T methodCall) {
    return MOCKITO_CORE.when(methodCall);
}
``` 

It is a convenient static function, so we can write a Kotlin function that wraps this and substitutes the
definition of `when` with `whenever`.

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

**Tip** You can also add a useful library to your codebase named [Mockito-Kotlin-Library](https://github.com/nhaarman/mockito-kotlin) which enables you to use mockito `when()` as `whenever()` but also has some good functions added to make testing Kotlin with Mockito a breeze. It just adds some simple syntactic sugar to Mockito anywhere it makes sense to do so to improve the way Mockito and Kotlin integrate.

### Write an application test

**Exercise**: Create an applicationtest for the Bootique

Convert the BootiqueApplicationTests.java file to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File).

This application stems from [start.spring.io](http://start.spring.io) and because of that it features a test setup already, for application tests. We are converting our codebase to Kotlin though, so that means we can't really leave this one in its Java form. That would just be silly. 

The first step would be to convert the test to Kotlin code, so do so now.

**Exercise**: Set up the Spring [WebEnvironment](https://spring.io/guides/gs/testing-web/) for application testing

We are going to test the app by calling an endpoint, so we'll be modifying the test. We are first going to tell Spring Boot to start a server (on a random port) and will wire in a TestRestTemplate to call the service.

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

Adapt the logic above to Kotlin for use in the `BootiqueApplicationTests`.

<details>
<summary>Suggested solution</summary>

Here's the Kotlin implementation for this:

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BootiqueApplicationTest {

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate 
```
</details>

Run the setup, the context should load, injection of the `TestRestTemplate` should succeed.

**Exercise**: Add the actual test

Let's define the test. Implement the body of this function, by using the `TestRestTemplate` to call the service. As we saw with the `when` function a while ago, using backticks allows you to specify method names that may clash with Kotlin keywords. You can also include whitespace in the name of the method -- meaning you can have expressive test names, which can be very helpful when testing.

```kotlin
@Test
fun `test bootique get products endpoint`() {}
```

The `/products` endpoint returns a list of products. In order to employ automatic conversion to List<Product> we can use a class called `ParameterizedTypeReference` which will use a typed return value for the template. Consider the following call which (thanks to the `ParameterizedTypeReference` will return a `List<Product>`:

```kotlin
testRestTemplate.exchange("/products", HttpMethod.GET, null, object: ParameterizedTypeReference<List<Product>>() {})
```

Create the test method and add the call listed above to it. Also add an assert to check if the first item in the list has a title with value `"iPhone X"`.

<details>
<summary>Suggested solution</summary>

The resulting test would look something like this:

```kotlin
@Test
fun `test bootique get products endpoint`() {
    val products = testRestTemplate.exchange("/products", HttpMethod.GET, null, object: ParameterizedTypeReference<List<Product>>() {}).body
    assertThat(products[0].title).isEqualTo("iPhone X")
}
```

The products val will be of type `List<Product>` thanks to the usage of the `ParametrizedTypeReference` which helps Spring work out the collection generic type for calls returning collections. This way the `exchange` method will return a `ResponseBody<List<Product>>`. It is a bit verbose though, and requires the use of an anonymous inner class, which in Kotlin is defined using `object: ParameterizedTypeReference<List<Product>>() {}`. This class does not define any abstract method so we can just provide an empty body, but we would have to provide it in every test method.

</details>

Run the test, it should run properly and succeed (provided you built it right) :)

**Exercise** Optimizing the test

As a final exercise, let's leverage three interesting features Kotlin has to offer: Extension functions and inlining + reified generics to shorten the resttemplate call.

What if we could define an (extension) function named `get` -- which uses reified generics -- to call the endpoint like this:

```kotlin
val products = testRestTemplate.get<List<Product>>("/products")
```

[Extension functions](https://kotlinlang.org/docs/reference/extensions.html) allow us to 'add' functionality to an already existing class. This is no bytecode magic, but merely some syntactic sugar that creates a function that takes the instance it is called on as an implicit parameter and exposes it as `this`.

Secondly, we can employ generics, more specifically [reified generics](https://kotlinlang.org/docs/reference/inline-functions.html), to extract the type from a generic parameter **at runtime**. Kotlin can inline function code at the call site, eliminating the need for dynamic calls but also adding some extra flexibility: by inlining the code at the call site, Kotlin is able to escape from the type erasure that haunts the JVM.

Try to implement an extension function for `TestRestTemplate`, named `get(uri: String)` which delegates to the `exchange()` method.

For reference, the syntax below shows an extension function on the `TestRestTemplate` type, in this case with a void return type. It also defines generics. Modify this call so you can call it like so: `val products = testRestTemplate.get<List<Product>>("/products"`.

```kotlin
fun <T> TestRestTemplate.get(url: String): T {
    return ...
}
``` 

<details>
<summary>Suggested solution</summary>

There are two main modifications required to the method to use reified generics. One is defining
the method to be inlined. You cannot reify generics without inlining, due to JVM type erasure.
We need to apply the `inline` keyword to the function.

Secondly, we need to add the `reified` keyword in the generic declaration, to define our 
intent to reify the generic type.

Having done this, we can now refer to the generic type as usual, but we can also extract the
type at runtime, which means we can actually use `T::class.java` to get the runtime type of 
the class!

Putting it all together, we can now conveniently call `get` with just the url path, and the specified generic type, like this: `testRestTemplate.get<List<Product>>("/products")`. We can use the generics to apply to the `ParametrizedTypeReference<T>`. 

```kotlin
inline fun <reified T> TestRestTemplate.get(url: String): T = this.exchange(
        url, HttpMethod.GET, null, object: ParameterizedTypeReference<T>() {}
)?.body ?: throw RuntimeException()
```

Run the test again, it should succeed! We now have a more compact way to call our endpoints. Please note that the body can theoretically be null, so we need to do something to prevent the compiler from complaining. For our testcases, we need the body to be defined at all times, so we can just throw a RuntimeException() to fail the test whenever this situation may occur.

But why the reified generics? The code compiled fine with and without the `inline` and `reified` keywords, so what is the difference? 

To see why, you can use the debugger. Debug the test with the function listed above and check what type is coming from the `RestTemplate.get` call. Looking at the code, this should be `List<Product>`, and at runtime this seems to be the case, as Intellij clearly lists that products is an `ArrayList` with 4 `Product` instances, just like we expected:

```
products = {java.util.ArrayList@7918}  size = 4
 0 = {com.bootique.bootique.Product@7920} "Product(id=1, title=iPhone X, brand=Apple, listPrice=989.99)"
 1 = {com.bootique.bootique.Product@7921} "Product(id=2, title=Galaxy S8, brand=Samsung, listPrice=699.99)"
 2 = {com.bootique.bootique.Product@7922} "Product(id=3, title=3310, brand=Nokia, listPrice=19.95)"
 3 = {com.bootique.bootique.Product@7923} "Product(id=4, title=Kermit, brand=KPN, listPrice=6.95)"
```

Now, define the extension function *without* the reified generics (remove the inline and reified keywords):

```kotlin
fun <T> TestRestTemplate.get(url: String): T = this.exchange(
        url, HttpMethod.GET, null, object: ParameterizedTypeReference<T>() {}
).body
```

Debug the test again, and look at the change -- even though the code suggests we are dealing with `List<Product>` the type erasure causes Spring to only be aware of the `List` part. So, we still get a `List` but it is populated with `LinkedHashMap` instances, containing all the individual values from the json, no `Product` instances. Not quite the same!

```
products = {java.util.ArrayList@7907}  size = 4
 0 = {java.util.LinkedHashMap@7909}  size = 4
  0 = {java.util.LinkedHashMap$Entry@7915} "id" -> "1"
  1 = {java.util.LinkedHashMap$Entry@7916} "title" -> "iPhone X"
  2 = {java.util.LinkedHashMap$Entry@7917} "brand" -> "Apple"
  3 = {java.util.LinkedHashMap$Entry@7918} "listPrice" -> "989.99"
 1 = {java.util.LinkedHashMap@7910}  size = 4
 2 = {java.util.LinkedHashMap@7911}  size = 4
 3 = {java.util.LinkedHashMap@7912}  size = 4
```

You'll also get an error when the test reaches the assertion: `java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to com.bootique.bootique.Product` - it will break!

This should show you the power of reified generics. We can now define extension functions that employ generics and still have enough type information available at runtime for the functions to be useful.

</details> 

## Bonus exercise ##

**Exercise** As a final exercise we can also test a post, to the products endpoint for example.This shows off the ability Kotlin has to interpolate (multiline) strings. We are going to be testing the endpoint that adds an article to the basket. Take the test below. Notice the multiline string, declared with `"""`. As with any string in Kotlin, we can use string interpolation to set values in the string directly. These are declared as `$productId` and `$quantity`. Anything that's accessible from the scope of the method can be used in String interpolation. You could also call methods, such as `${productId.toUpperCase()}` for instance.

```kotlin
@Test
fun `add product to basket`() {
    val productId = "1"
    val quantity = 2

    val response = testRestTemplate.postJson<String>("/baskets/1/items", """
            {
                "productId":"$productId",
                "quantity": $quantity
            }
    """)

    assertThat(response.statusCode.value()).isEqualTo(200)
}
```

Now, as an assignment, build the implementation of the extension function `postJson()`. Here's something to get you started. Notice the type of the response is omitted here, and has to be determined in the method call. You can get this to work without modifying the test code, all works needs to be done inside the extension function.

As a hint: reified generics can help you out here too!

```
fun <T> TestRestTemplate.postJson(url: String, json: String): ResponseEntity<T> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    val entity = HttpEntity(json, headers)
    return this.postForEntity(url, entity, T)
}
```

<details>
<summary>Suggested solution</summary>
    
The function `postJson` will not compile, as `T` is out of place; this function requires a concrete class.    

Reified generics to the rescue again! When using reified generics, you don't only get to hold on to the type information at runtime, as an added bonus you can also extract the class from the generic type, which is nice to define the return type here. Using `T::class.java` will return `java.lang.String` in this case, as we are calling it from the test with `String` as the defined generic type. Thanks to Kotlin inlining the function we still know the generic type used at runtime.

```kotlin
inline fun <reified T> TestRestTemplate.postJson(url: String, json: String): ResponseEntity<T> {
    val headers = HttpHeaders()
    headers.contentType = MediaType.APPLICATION_JSON
    val entity = HttpEntity(json, headers)
    return this.postForEntity(url, entity, T::class.java)
}
```

**Important** 

In the earlier example for `get` we used:

`this.exchange(url, HttpMethod.GET, null, object: ParameterizedTypeReference<T>() {})` 

Instead of: 

`this.exchange(url, HttpMethod.GET, null, T::class.java)`

which is valid code when using reified generics. Why not use the latter as it is shorter and does not require the use of an anonymous class?

The answer is: because we can't. If we don't use the ParametrizedTypeReference we will lose some relevant typing information. Let's say we are calling this function with `List<Product>` as `T`. Using `T::class.java` will resolve to `List<Object>` at runtime, meaning we've lost `List<Product>` along the way and Spring will not be able to convert to the right object for you. On the JVM, generic type information for classes is not lost so creating an instance of `ParametrizedTypeReference<T>` will allow Spring to still resolve that the intended type was `List<Product>`. T

Writing the code like this gives us a solution that works for domain types such as `Product` and lists of these types such as `List<Product>` alike. Give the two variants a try to see what happens for yourself.

</details> 

## That's it for now ##

That's it, you've done it!

Of course, we are well aware that these tests are somewhat representative of the real-world tests you'll be building, but lack refinement. We hope this will give you the insights you'll need to be able to write some solid tests in Kotlin and at the same time leverage the language features to reduce the volume of code you'll need to write to achieve this.

**So, we encourage you to experiment, experiment and experiment some more. Kotlin might just be that Java replacement you didn't know you'd like so much.**

For the final implementation of this service including the tests above in Kotlin, checkout the `final` branch.

## Thank you for participating! ##

- Questions? Come find us, we'll do all we can to clarify anything unclear. 
- Looking for a job, a change of scenery? Get in touch with us so we can discuss the possibilities.
- Want us to provide this workshop for your whole project team? Let us know, we'll make it happen! If you provide the location, we'll provide the material. No additional costs.
