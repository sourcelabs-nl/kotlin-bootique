## Exercise 2: convert more code to Kotlin

In this exercise we will convert the Product.java and OrderItem.java classes to Kotlin, our goal is to make it more concise.

### Convert Product.java to Kotlin

Open Product.java.

**Exercise**: convert Product.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

Wow, that was easy. The resulting file is what we call concise :-)

### Convert OrderItem.java to Kotlin

Open OrderItem.java. 

**Exercise**: convert OrderItem.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

The outcome of the conversion is far from optimal, because of the Java class being a bit more complex. We can do better! 

Let get rid of the equals(), hashCode() and toString() boiler-plate. Kotlin has a feature for that: [data classes](https://kotlinlang.org/docs/reference/data-classes.html). Lets do this step by step.

**Exercise**: convert OrderItem to a data class by adding the `data` keyword

With data classes we get the `equals()`, `hashCode()` and `toString()` functions for free, because the Kotlin compiler generates them for a data class.
 
**Exercise**: remove the `equals()`, `hashCode()` and `toString()` functions.

In the converted code we ended up with a [constructor](https://kotlinlang.org/docs/reference/classes.html#constructors) on class level called the primary constructor, and next to that the secondary constructor.

In many situations we can get rid of the duplicate constructors by merging the [constructors](https://kotlinlang.org/docs/reference/classes.html#constructors) into a single primary constructor.
 
Notice that the `productId` argument in the two constructors are different, one which is the non-nullable `val productId: String` and the other which is nullable `val productId: String?`. Since productId should be a mandatory field, we will use the null safe version in the next exercise.
 
**Exercise**: merge the two constructors into primary constructor but *don't remove* the @JsonCreator and @JsonProperty annotations. Hint, the constructor keyword on class level is mandatory when you want to use annotations on the primary constructor.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity))
}
```
</details>
<br>

#### Verify the changes

Build the project with maven `./mvnw clean verify`, the build should succeed.

Start the application `./mvnw spring-boot:run` and see if the application is still working. 

**Exercise**: execute the following curl command on the terminal:

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
```

We broke the application :-( There should be an exception in the application logs:

```
Failed to read HTTP message: org.springframework.http.converter.HttpMessageNotReadableException: 
JSON parse error: Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type; 
nested exception is com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: 
Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type
at [Source: (PushbackInputStream); line: 1, column: 30] (through reference chain: com.bootique.bootique.OrderItem["price"])
```

In the POST body we send only two fields `{"productId":"1","quantity":2}` for an OrderItem, these fields are directly mapped onto the OrderItem class. This used to work when there were two constructors, but after the merge we ended up with a single constructor which requires 3 non-nullable (mandatory) parameters. 

How can we fix this with Kotlin? First lets try to make the price field (optional) nullable and add the ? after the BigDecimal type. You will probable notice that the totalPrice calculation is now also giving you a hard time. Price can now be nullable therefore we need to add null safety checks using the ? operator in the totalPrice calculation, see the snippet below:
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```

A better approach would be to avoid having to deal with null values all together so that way we don't have to worry about potential NPEs. We can do this by providing a default value for the price argument. This way we dont have to explicitly provide a value, but it will never be null. In the Java version price was assigned the value of BigDecimal.ZERO, use that here as well. 

**Exercise**: assign a default value, BigDecimal.ZERO to the price argument.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity)) // evaluated every time we access the totalPrice property or call getTotalPrice() from Java.
}
```
</details>
<br>

In the code snippet above the constructor arguments are val, immutable, which means after assignment the value cannot be changed. The `val totalPrice` is calculated based in these immutable properties, because of this we can simplify the code and write the totalPrice calculation as an expression. Would it not be nice if we could write it like:

```kotlin
val totalPrice: BigDecimal = price * quantity
```

Or even more concise like this:

```kotlin
val totalPrice = price * quantity
```

Lets do this step by step! 

**Exercise:** change the totalPrice calculation to the snippet below:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

This is not yet how we want to write the expression because we still have to wrap the quantity in a BigDecimal in order to be able to do calculations with BigDecimals. Notice that in Java this way of doing calculations on BigDecimals would not be possible!

The Kotlin stdlib includes [overloaded operators](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/java.math.-big-decimal/index.html) for Java types like java.math.BigDecimal, this allows use the * (times) operator on BigDecimal. 

Have a look at the signature for times operator on java.math.BigDecimal. 

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

Note that `price * BigDecimal(quantity)` under the hood is exactly the same as `price.times(BigDecimal(quantity))`, this is just syntactic sugar. 

What we want is being able to invoke the times function with a Int argument so that we do not need to wrap our Int in a BigDecimal. We can implement our own overloaded operator, similar to the one in the Kotlin stdlib. 

**Exercise**: write an operator for java.math.BigDecimal that accepts an Int.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
operator fun BigDecimal.times(quantity: Int) = this.times(BigDecimal(quantity))
```
</details>
<br>

You can group these custom extensions like overloaded operators in a Kotlin files, so that they are easily recognizable and can be shared and reused in other parts of the code.

### Polishing the code

This application communicates over HTTP using JSON, as you have seen in Swagger or in the curl command in the README.md. The JSON (de)serialization in this application is handled by the Jackson library, the spring-boot-starter-web (Spring Boot 2!) dependency pulls in the Jackson dependencies for us.

In the BasketController the JSON data is mapped from the POST data directly on the OrderItem class. As you can see, in the OrderItem class we are instructing the Jackson library, with the @JsonCreator and @JsonProperty, how to map the JSON data to our Java (or Kotlin) class. 

Reason for having the @JsonProperty annotation is that when compiling Java code, the parameter names of the constructor parameters are lost, so Jackson does not know how to map the json properties to the OrderItem class. In Kotlin, constructor parameter names are preserved when compiling code. We can therefore get rid of the @JsonProperty annotations. 

As a bonus feature, the Jackson library also allows us to omit the @JsonCreator annotation when using Kotlin (these features are provided by the jackson kotlin module). This needs to explicitly be added to the project dependencies!

When we don't have any annotations on the class definition we can omit the constructor keyword as well.

**Exercise**: add the `jackson-module-kotlin` to the maven `pom.xml` (note that `${jackson.version}` is defined by Spring Boot)

```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
    <version>${jackson.version}</version>
</dependency>
```

**Exercise**: cleanup the OrderItem class by removing the Jackson annotations and constructor keyword.

<details>
  <summary>Suggested solution:</summary>

```kotlin
data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice = price * quantity
}
```
</details>

<br>

**Exercise**: Build and restart the application and see if it still works by adding some more items to your basket.

### Next steps

Continue with [exercise-3](exercise-3.md):

You can either start fresh by switching to the exercise-3 branch or continue on your current branch.

Switching to the exercise-3 branch can be done using IntelliJ or in your terminal by issuing the following command:

```
git checkout exercise-3
```
