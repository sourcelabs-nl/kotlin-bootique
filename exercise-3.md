## Exercise 3: 100% to Kotlin

In this exercise we will convert the remaining Java classes to Kotlin, covering some of the Java to Kotlin conversion quirks related to Java streams.

### Convert Basket.java to Kotlin

Open `Basket.java`

**Exercise**: convert `Basket.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

After the conversion the code is still far from optimal. The Java2kotlin converter still uses the Java Stream API and tries to make the best out of it. 

We could clean it up a bit by writing the calculation in exactly the same way as we would do with the Java Stream API. But Kotlin has a build in reduce function in the std lib, so we can use that instead of the Java Stream API.

```kotlin
val totalPrice: BigDecimal
    get() = items.map(OrderItem::totalPrice).reduce { acc, next -> acc + next }
```

**Exercise:** change the `totalPrice` calculation to the snippet above

There are a few problems with this code. First, the map operation produces a new list as its result on which the reduce operation is performed while we actually don't even use this list anywhere else. 
Our newly introduced reduce does not work on an empty collection. Even though the code compiles, it is effectively broken. 

We could better use fold here, as it will return the provided default value whenever we try to reduce an empty collection. We can also directly call fold on the items without the intermediate map operation. 

```kotlin
val totalPrice: BigDecimal 
    get() = items.fold(BigDecimal.ZERO, { acc, next -> acc.plus(next.totalPrice) })
```

**Exercise:** change the calculation to the snippet above

You could argue if the above is more concise than the Java Stream API version. A more idiomatic way of writing this would be:

```kotlin
val totalPrice: BigDecimal 
    get() = items.sumBy { item -> item.totalPrice }
```

We could even get rid of the item declaration since kotlin exposes a variable called it which refers to the current item.

```kotlin
val totalPrice: BigDecimal 
    get() = items.sumBy { it.totalPrice }
```

Kotlin already has build-in functions to sum BigDecimal types:

```kotlin
public inline fun <T> Iterable<T>.sumOf(selector: (T) -> java.math.BigDecimal): java.math.BigDecimal
```

**Exercise**: Adjust the code to use the build-in sumOf() function.

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
val totalPrice: BigDecimal
    get() = items.sumOf { it.totalPrice }
```
</details>
<br>

### Property syntax vs functions

Kotlin encourages you to use the [property syntax](https://kotlinlang.org/docs/reference/properties.html) whenever possible. In some situations you could also prefer/use a [expression function](https://kotlinlang.org/docs/reference/functions.html#single-expression-functions) to achieve similar results.

**Exercise**: Try to write the totalPrice calculation as a single expression function called `fun getTotalPrice()`.

<details>
  <summary>Suggested solution:</summary>

```kotlin
fun getTotalPrice() = items.sumOf { it.totalPrice }
```
</details>
<br>

Still there is some noise in the file caused by the constructors. We can combine the following code in the primary constructor for the class:

```kotlin
private val items: MutableList<OrderItem>

constructor() {
    items = ArrayList()
}

constructor(items: MutableList<OrderItem>) {
    this.items = items
}
```

**Exercise**: Write a primary constructor for the Basket class and combine the constructors into one property for the items. Verify that the code still works!

<details>
  <summary>Suggested solution:</summary>

```kotlin
class Basket(private val items: MutableList<OrderItem> = mutableListOf()) {

    fun getItems(): List<OrderItem> = items.toList()

    fun addOrderItem(orderItem: OrderItem) = items.add(orderItem)

    fun getTotalPrice(): BigDecimal = items.sumOf { it.totalPrice }
} 
```
</details>


### Convert BasketRepository.java to Kotlin

Open `BasketRepository.java`

**Exercise**: convert `BasketRepository.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

First have a look at the `getBasketById()`, it uses `computeIfAbsent`. See if you can find a method in the Kotlin standard library (`kotlin.collections`) which could serve as a nice replacement for `computeIfAbsent`.

**Exercise**: replace `computeIfAbsent` by a more concise Kotlin alternative

<details>
  <summary>Suggested solution:</summary>

```kotlin
fun getBasketById(id: String): Basket = baskets.getOrPut(id) { Basket() }
```
Or without the need for a Lambda:

```kotlin
fun getBasketById(id: String): Basket = baskets.getOrDefault(id, Basket())
```

</details>
<br>

Note that the `baskets: ConcurrentHashMap` is wrapped in a `companion object { }`.

```kotlin
companion object {
    private val baskets = ConcurrentHashMap<String, Basket>()
}
```

The original Java code defined the baskets variable as static but [Kotlin does not support the static keyword](https://discuss.kotlinlang.org/t/what-is-the-advantage-of-companion-object-vs-static-keyword/4034). You can use companion object instead which is like a singleton Object associated with a certain class. Alternatively you could define `private val baskets = ConcurrentHashMap<String, Basket>()` outside of the class which has the same semantics as static in Java.

### Convert ProductRepository.java to Kotlin

Open `ProductRepository.java`

**Exercise**: convert `ProductRepository.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

After converting to Kotlin it is arguable that the Kotlin versions looks cleaner than its Java counterpart. Let's try to improve this.

Instead of the `getProducts()` function we ended up with a property called `products`. While this might be the preferred way in Kotlin, it does not look pretty here. Let's convert it back to a function.

**Exercise**: change the property `products` to a function `fun getProducts()` with only an expression body. While doing so, remove the copyOf wrapper and use Kotlin's `toList` function.

<details>
<summary>Suggested solution:</summary>

```kotlin
fun getProducts(): List<Product> = Companion.products.values.toList()
```
</details>

By changing the code we got rid of the name clash between the property `products` in the ProductRepository and the companion object field called `products`. We can remove the `Companion.` prefix from the `getProductById` and `getProducts` function body.

Lets work a bit on the concise syntax.

**Exercise**: rewrite the function body `fun getProductById()` into an expression.

<details>
<summary>Suggested solution:</summary>

```kotlin
fun getProductById(productId: String): Product? = products[productId]
```
</details>
<br>

Last but not least, use the Kotlin version of `Map.of` which is `mapOf()` instead.

**Exercise**: rewrite `Map.of` to `mapOf()`.

<details>
<summary>Suggested solution:</summary>

```kotlin
private val products = mapOf(
    "1" to Product("1", "iPhone XX", "Apple", BigDecimal("3989.99")),
    "2" to Product("2", "Galaxy S25", "Samsung", BigDecimal("2699.99")),
    "3" to Product("3", "3310", "Nokia", BigDecimal("19.95")),
    "4" to Product("4", "Kermit", "KPN", BigDecimal("6.95"))
)
```
</details>
<br>

### Convert BootiqueController.java to Kotlin

Open `BootiqueController.java`

**Exercise**: convert `BootiqueController.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

If there are any issues with the imports, then remove the unused failing imports.

**Exercise**: Rewrite the controller methods to expression functions if possible.

One thing to notice is the `getBasket` method signature:

```kotlin
fun getBasket(@PathVariable("id") id: String?): Basket = basketRepository.getBasketById(id!!)
```

While `@PathVariable` is required by default, the id argument is nullable here. This is because it was converted from Java and there was no information available for the converter (@NotNull annotation) to set it to not null.

**Exercise**: Change the `id` argument to the non-nullable type String and in the expression body remove the non-null assertion. Do the same for `addToBasket()`.

As a bonus we could omit the `"id"` from `@PathVariable("id")` because Spring is able to figure out the name of the argument from the Kotlin generated bytecode.

In `addToBasket` we improve on handling the case where we try to add a non-existing productId to our basket. 

**Exercise**: Add a null check for a non-existing product and throw an ResponseStatusException() if not found.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
@PostMapping(path = ["/baskets/{id}/items"], consumes = [MediaType.APPLICATION_JSON_VALUE])
fun addToBasket(@PathVariable("id") id: String, @RequestBody orderItem: OrderItem): Basket {
    val productById = productRepository.getProductById(orderItem.productId)
        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Product with id: ${orderItem.productId} not found.")
    val basket = basketRepository.getBasketById(id)
    basket.addOrderItem(orderItem.copy(price = productById.listPrice))
    return basket
}
```
</details>
<br>

Last but not least we have the BootiqueApplicationTests class. Once this test class has been converted we could get rid of the Java compiler if needed.

**Exercise**: Convert BootiqueApplicationTests to Kotlin. Build the application and verify everything is still working as expected!

### Next steps

You have now successfully converted all the Java application code to Kotlin! 

Continue with [exercise-4](exercise-4.md):

You can either start fresh by switching to the exercise-4 branch or continue on your current branch.

Switching to the exercise-4 branch can be done using IntelliJ or in your terminal by issuing the following command:

```
git checkout exercise-4
```
