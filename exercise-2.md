## Exercise 2: convert more code to Kotlin

In this exercise we will convert the `Product.java` and `OrderItem.java` classes to Kotlin, our goal is to make it more concise.

### Convert Product.java to Kotlin

Open `Product.java`.

**Exercise**: convert Product.java to Kotlin using IntelliJ _menu > Code > Convert Java File to Kotlin File_. 

Wow, that was easy. The resulting file is what we call concise :-)

### Convert OrderItem.java to Kotlin

Open `OrderItem.java`. 

**Exercise**: convert OrderItem.java to Kotlin using IntelliJ _menu > Code > Convert Java File to Kotlin File_. 

The outcome of the conversion is far from optimal, because of the Java class being a (little) bit more complex. We can do better! 

Let get rid of the `equals()`, `hashCode()` and `toString()` boiler-plate. Kotlin has a feature for that: [data classes](https://kotlinlang.org/docs/reference/data-classes.html). Lets do this step by step.

**Exercise**: convert OrderItem to a data class by adding the `data` keyword

With data classes we get the `equals()`, `hashCode()` and `toString()` functions for free, because the Kotlin compiler generates them for a data class.
 
**Exercise**: remove the `equals()`, `hashCode()` and `toString()` functions.

In the converted code we ended up with a primary [constructor](https://kotlinlang.org/docs/reference/classes.html#constructors) and next to that a secondary constructor.

In many situations we can get rid of the generated duplicate constructors by merging the [constructors](https://kotlinlang.org/docs/reference/classes.html#constructors) into a single primary constructor by providing default values for some of the constructor arguments.
 
Notice that the `productId` and `price` arguments in the two constructors are of the nullable type String.  
 
**Exercise**: merge the two constructors into primary constructor but *don't remove* the `@JsonCreator` and `@JsonProperty` annotations. Hint, you can also use the constructor keyword on class level when you want to use annotations on the primary constructor just yet. Make all arguments non-nullable and don't provide a default value.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(
    @JsonProperty("productId") val productId: String,
    @JsonProperty("quantity") val quantity: Int,
    val price: BigDecimal
) {

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

In the POST body we send only two fields `{"productId":"1","quantity":2}` for an OrderItem, these fields are directly mapped onto the OrderItem class. This used to work when there were two constructors, but after the merge we ended up with a single constructor which requires 3 non-nullable (mandatory) arguments. 

How can we fix this with Kotlin? First lets try to make the price field nullable and add the question mark to the BigDecimal type. The totalPrice calculation uses the price property and should handle the value being null by adding the null-safe operator (?.) to the totalPrice calculation, like in the snippet below:
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```

A much better approach would be to avoid having to deal with null values all together and not having to worry about potential NPEs. 
We can do this by providing a default value for the price argument. This way we can construct the OrderItem without explicitly providing a value for price, but it will never be null. In the Java version price was assigned the value of BigDecimal.ZERO, use that here as well. 

**Exercise**: assign a default value, BigDecimal.ZERO to the price argument.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice: BigDecimal
        get() = price.multiply(BigDecimal(quantity)) 
}
```
</details>
<br>

In the code snippet above the constructor arguments are all immutable now, which means that after initialization their values cannot be changed. 

The `val totalPrice` which is called a property in Kotlin, calculates the totalPrice based on two immutable properties (price and quantity). Since both properties are `vals` (immutable) we can get rid of the get() method on the property. Getters on properties are useful when the expression needs to be evaluated every time the property is accessed.  

But the solutin is not yet the idiomatic Kotlin way of writing code, because it would be even nicer if we could write it like:

```kotlin
val totalPrice: BigDecimal = price * quantity
```

Lets do this step by step (complete all the exercises before restarting the application).

**Exercise:** instead of multiply use the times operator and change the totalPrice calculation to the snippet below:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

We still need to wrap the quantity property in a BigDecimal in order to be able to do calculations with BigDecimal instances.

Note that performing these kinds of calculations with BigDecimals is not possible in Java since it does not support operator overloading! In Kotlin this is possible, but limited to a few predefined operators only. The Kotlin stdlib includes [overloaded operators](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/java.math.-big-decimal/index.html) for Java types like java.math.BigDecimal, this allows use the * (times) operator on BigDecimal. 

Now have a look at the signature for times operator on `java.math.BigDecimal` in IntelliJ (Cmd+Click). 

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

Note that `price * BigDecimal(quantity)` under the hood is identical to `price.times(BigDecimal(quantity))`, consider it to be syntactic sugar. 

What we want is being able to invoke the times function with a Int argument so that we do not need to wrap our Int in a BigDecimal. We can implement our own overloaded operator, similar to the one in the Kotlin stdlib. 

**Exercise**: write an operator for java.math.BigDecimal that accepts an Int.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
operator fun BigDecimal.times(quantity: Int) = this.times(BigDecimal(quantity))
```
</details>
<br>

You can add the overloaded operator to the Kotlin file from which you are using it, but you can also group extensions and overloaded operators in a separate Kotlin file so that they are easily recognizable and can be shared and reused in other parts of the code.

### Polishing the code

This application communicates via JSON over HTTP, as you have seen in Swagger or in the curl command in the README.md. The JSON (de)serialization in this application is handled by the Jackson library, the spring-boot-starter-web (Spring Boot 2!) dependency pulls in the Jackson dependencies for us.

In the BasketController the JSON data is mapped from the POST data directly on the OrderItem class. As you can see, in the OrderItem class we are instructing the Jackson library, with the `@JsonCreator` and `@JsonProperty`, how to map the JSON data to our Java (or Kotlin) class. 

Reason for having the `@JsonProperty` annotation is that when compiling Java code, the parameter names in the constructor are lost, Jackson does not know how to map the json properties to the OrderItem class. In Kotlin, parameter names are preserved when compiling the code and stores as meta data. We can therefore get rid of the  `@JsonProperty` annotations. As a bonus feature, the Jackson library also allows us to omit the `@JsonCreator` annotation when using Kotlin. 

This functionality is provided by the `jackson-kotlin-module`. This dependency needs to be added explicitly to the maven pom.xml!

Once we have no `@JsonCreator` annotations on the `OrderItem` class we can omit the Kotlin constructor keyword as well.

**Exercise**: add the `jackson-module-kotlin` to the maven `pom.xml` (note that the version of this module is [managed by Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-dependency-versions.html)).

```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-kotlin</artifactId>
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
