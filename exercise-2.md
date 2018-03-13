## Exercise 2: convert more code to Kotlin

In this exercise we will convert the Product.java and OrderItem.java classes to Kotlin, our goal is to make it more concise.

### Convert Product.java to Kotlin

Open Product.java.

**Exercise**: convert Product.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

Wow, that was easy :-) 

### Convert OrderItem.java to Kotlin

Open OrderItem.java. 

**Exercise**: convert OrderItem.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

The outcome of the conversion is far from optimal, we can do way better! Remember Kotlin [data classes](https://kotlinlang.org/docs/reference/data-classes.html)? Let get rid of the boiler-plate and convert the OrderItem class to a data class. 

**Exercise**: convert OrderItem to a data class

With data classes we get the equals, hashCode and toString method for free.
 
**Exercise**: delete the equals, hashCode and toString methods, because the compiler generates them for you now it is a data class.

In the converted code we ended up with a [constructor](https://kotlinlang.org/docs/reference/classes.html#constructors) on class level called the primary constructor, and an overloaded version inside the class called the secondary constructor.

In many situations we can get rid of overloaded constructors by merging the two [constructors](https://kotlinlang.org/docs/reference/classes.html#constructors) into a single primary constructor.
 
For productId there are two variants after the conversion. One which is the non-nullable `val productId: String` and the other which is nullable `val productId: String?`. Since productId is a mandatory field, use the null safe version in the next exercise.
 
**Exercise**: try to merge the two constructors into one but *keep* all the @JsonCreator and @JsonProperty annotations. Hint, the constructor keyword on class level is mandatory when you want to use annotations on the primary constructor.

<details>
  <summary>The resulting code should look like this:</summary>
  
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

Start the application (./mvnw spring-boot:run) and see if the application still works. 

Now execute the following curl command on the terminal:

```                                                                                                                                                                                                                                                                                                                                                            
curl -H "Content-Type: application/json" -X POST -d '{"productId":"1","quantity":2}' http://localhost:8080/baskets/1/items
```

We should see an exception in the application logs:

```
Failed to read HTTP message: org.springframework.http.converter.HttpMessageNotReadableException: 
JSON parse error: Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type; 
nested exception is com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: 
Instantiation of [simple type, class com.bootique.bootique.OrderItem] value failed 
for JSON property price due to missing (therefore NULL) value for creator parameter price which is a non-nullable type
at [Source: (PushbackInputStream); line: 1, column: 30] (through reference chain: com.bootique.bootique.OrderItem["price"])
```

We broke the application :-( Remember we merged the two constructors? In the POST body we send only two fields `{"productId":"1","quantity":2}` for an OrderItem, this used to work when there were overloaded constructors. After the merge we ended up with a single constructor which requires 3 non-nullable (mandatory) parameters. 

How can we fix this with Kotlin? First try to make the price field nullable and add the ? after the BigDecimal type. You will probable notice that the totalPrice calculation is now also giving you a hard time. Price can now be nullable therefore you need to add null checks in the totalPrice calculation.

<details>
  <summary>Example with nullable price:</summary>
  
```kotlin
data class OrderItem @JsonCreator constructor(@JsonProperty("productId") val productId: String, 
                                              @JsonProperty("quantity") val quantity: Int, 
                                              val price: BigDecimal?) {
    val totalPrice: BigDecimal?
        get() = price?.multiply(BigDecimal(quantity))
}
```
</details>
<br>

A better approach would be to avoid having to deal with null values, this way we do not have to worry about potential NPEs. We can do this by providing a default value for the price, in the Java version price was assigned the value of BigDecimal.ZERO, use that here as well. 

**Exercise**: assign the default value to the price property, restart the application and try to run the same curl command as before.

<details>
  <summary>Example with a default value for price:</summary>
  
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

In the code snippet above the constructor arguments are val, immutable, which means after assignment the value cannot be changed. totalPrice is calculated based in this immutable properties, because of this we can also write the totalPrice assignment as an expression. Would it not be nice if we could write it like:

```kotlin
val totalPrice: BigDecimal = price * quantity
```

Or without the explicit type, the type inference provided by Kotlin can help you here:

```kotlin
val totalPrice = price * quantity
```

This syntax can be achieved by using [operator overloading](https://kotlinlang.org/docs/reference/operator-overloading.html). The Kotlin stdlib includes [overloaded operators](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/java.math.-big-decimal/index.html) for Java types like java.math.BigDecimal. This allows use to write the above statement like:

```kotlin
val totalPrice: BigDecimal = price * BigDecimal(quantity)
```

**Exercise**: replace the totalPrice calculation with the snippet above.

This is not yet how we want to write the expression because we still have to wrap the quantity in a BigDecimal in order to use the operator. Lets look at the signature for times operator on java.math.BigDecimal. 

```kotlin
public operator inline fun java.math.BigDecimal.times(other: java.math.BigDecimal): java.math.BigDecimal
```

As you might have noticed `price.times(BigDecimal(quantity))` is the same as `price * BigDecimal(quantity)`, this is just syntactic sugar. We want to be able to invoke the times function with a Int argument so that we do not need to wrap it in a BigDecimal. Therefore we need to implement our own overloaded operator, similar to the one in the Koltin stdlib. 

**Exercise**: write an operator for java.math.BigDecimal that accepts an Int.

<details>
  <summary>The resulting code should look like this:</summary>
  
```kotlin
operator fun BigDecimal.times(quantity: Int) = this.times(BigDecimal(quantity))
```
</details>
<br>
It is a good practise to group these type of (language) extensions in a separate, easily recognizable, file so it can be shared or reused in other parts of the code.

### Polishing the code

This application communicates over HTTP using JSON, as you have seen in Swagger or in the curl command in the introduction.md. The JSON (de)serialization in this application is handled by the Jackson library, the spring-boot-starter-web (Spring Boot 2!) dependency pulls in all these Jackson dependencies for us.

In the BasketController the JSON data is mapped from the POST data directly on the OrderItem class. As you can see, in the OrderItem class we are instructing the Jackson library, with the @JsonCreator and @JsonProperty, how to map the JSON data to our Java (or Kotlin) class. 

Reason for having the @JsonProperty annotation is that when compiling Java code, the parameter names of the constructor parameters are lost, so Jackson does not know how to map the json properties to the OrderItem class. In Kotlin, constructor parameter names are preserved when compiling code. We can therefore get rid of the @JsonProperty annotations. 

As a bonus feature, the Jackson library also allows us to omit the @JsonCreator annotation when using Kotlin (these features are provided by the jackson kotlin module).

We can omit the constructor keyword as well when there are no annotation needed for the constructor declaration.

**Exercise**: cleanup the code by removing the Jackson annotations and constructor keyword.

<details>
  <summary>The resulting polished data class looks like:</summary>

```kotlin
data class OrderItem(val productId: String, val quantity: Int, val price: BigDecimal = BigDecimal.ZERO) {
    val totalPrice = price * quantity
}
```
</details>

<br>

**Exercise**: Build and restart the application and see if it still works!

### Done?

You could consider converting the Product class to a data class so we get the equals, hashCode and toString for free.

### Next steps

Continue with [exercise-3](exercise-3.md):

```
git checkout exercise-3
```
