## Exercise 3: 100% to Kotlin

In this exercise we will convert the remaining Java classes to Kotlin, covering some of the Java to Kotlin conversion quirks related to Java streams.

### Convert Basket.java to Kotlin

Open `Basket.java`

**Exercise**: convert `Basket.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

After the conversion the code is broken, just take a look at the totalPrice calculation. Apparently IntelliJ is not able to figure out all the Java Stream API operations and tries to make the best out of it. 

We can fix this by writing the calculation in exactly the same way as we would do with the Java Stream API:

```kotlin
val totalPrice: BigDecimal        
    get() = items.stream().map(OrderItem::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
```

**Exercise:** change the calculation to the snippet above

Kotlin has a richer (functional) API which allows us write this in a different way without using the Java Stream API, for example we could achieve the same using the fold function:

```kotlin
val totalPrice: BigDecimal 
    get() = items.fold(BigDecimal.ZERO, { sum, item -> sum.plus(item.totalPrice) })
```

**Exercise:** change the calculation to the snippet above

You could argue if the above is more concise than the Java Stream API version. A more concise way of writing this would be:

```kotlin
val totalPrice: BigDecimal 
    get() = items.sumBy { item -> item.totalPrice }
```

We could even get rid of the item declaration since kotlin exposes a variable called it which refers to the current item.

```kotlin
val totalPrice: BigDecimal 
    get() = items.sumBy { it.totalPrice }
```

Kotlin already has build-in functions to sum Int and Double types but not for BigDecimal:

```kotlin
public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int 

public inline fun <T> Iterable<T>.sumByDouble(selector: (T) -> Double): Double
```

**Exercise**: write an extension function which allows for summing BigDecimals. Hint: look at the implementation of `public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int` above

<details>
  <summary>Suggested solution:</summary>
  
```kotlin
fun <T> Iterable<T>.sumBy(selector: (T) -> BigDecimal): BigDecimal {
    var sum = BigDecimal.ZERO
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
```
</details>
<br>

### Property syntax vs functions

Kotlin encourages you to use the [property syntax](https://kotlinlang.org/docs/reference/properties.html) whenever possible. In some situations you could also prefer/use a [expression function](https://kotlinlang.org/docs/reference/functions.html#single-expression-functions) to achieve similar results.

**Exercise**: Try to write the totalPrice calculation as a single expression function called `fun getTotalPrice()`.

<details>
  <summary>Suggested solution:</summary>

```kotlin
fun getTotalPrice() = items.sumBy { it.totalPrice }
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

**Exercise**: Write a primary constructor for the Basket class which has a single property items: MutableList<OrderItem>. Does the code still work?

<details>
  <summary>Suggested solution:</summary>

```kotlin
class Basket(val items: MutableList<OrderItem> = mutableListOf()) 
```
</details>


### Convert BasketRepository.java to Kotlin

Open `BasketRepository.java`

**Exercise**: convert `BasketRepository.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

Two interesting things about the converted code. First have a look at the getBasketById(), there is some explicit casting into a Map.

**Exercise**: remove the cast to `java.util.Map<String, Basket>`

See if you can find a method in the Kotlin standard library (`kotlin.collections`) which could serve as a nice replacement for `computeIfAbsent`.

**Exercise**: replace `computeIfAbsent` by a more concise Kotlin alternative

<details>
  <summary>Suggested solution:</summary>

```kotlin
fun getBasketById(id: String): Basket = baskets.getOrPut(id) { Basket() }
```

Or without the use of type inference:

```kotlin
fun getBasketById(id: String) = baskets.getOrPut(id) { Basket() }
```
</details>
<br>

Note that the `baskets: ConcurrentHashMap` is wrapped in a `companion object { }`.

```kotlin
companion object {
    private val baskets = ConcurrentHashMap<String, Basket>()
}
```

The original Java code defined baskets as static but [Kotlin does not support the static keyword](https://discuss.kotlinlang.org/t/what-is-the-advantage-of-companion-object-vs-static-keyword/4034). You can use companion object instead which is like a singleton Object associated with a certain class. Alternatively you could define `private val baskets = ConcurrentHashMap<String, Basket>()` outside of the class which has the same semantics as static in Java.

### Convert ProductRepository.java to Kotlin

Open `ProductRepository.java`

**Exercise**: convert `ProductRepository.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

You will notice that getProductById() is broken, this is because the expected return can actually be null but the method defines a NonNullable return type: Product. The implementation might return a null value when the product cannot be found.

**Exercise**: change the return type of `fun getProductById()` that it allows for a returning nullable Product.

<details>
<summary>Suggested solution:</summary>

```kotlin
fun getProductById(productId: String): Product? {
    return products[productId]
}
```
</details>
<br>

If the Java code would have looked like the snippet below, with the `@Nullable`, the conversion would have succeeded:

```java
@Nullable
public Product getProductById(String productId) {
    return products.get(productId);
}
```

Lets work a bit on the concise syntax.

**Exercise**: rewrite the function `fun getProductById()` as an single expression function.

<details>
<summary>Suggested solution:</summary>

```kotlin
fun getProductById(productId: String) = products[productId]
```
</details>
<br>

The implementation of `fun getProducts(): List<Product>` was translated from Java, but we can improve this the Kotlin way. The idea behind the Java implementation was to always return an immutable List of Products so that it can never be modified outside of the class. In Kotlin we can return the products.values and convert that to a List, which is immutable by default.

**Exercise**: Change `fun getProducts(): List<Product>` in a way that it returns products.values as a Kotlin (immutable) List 

<details>
<summary>Suggested solution:</summary>

```kotlin
fun getProducts() = products.values.toList()
```
</details>
<br>

### Convert BootiqueController.java to Kotlin

Open `BootiqueController.java`

**Exercise**: convert `BootiqueController.java` to Kotlin using IntelliJ (_menu > Code > Convert Java File to Kotlin File_). 

The resulting code looks pretty ok.

**Exercise**: Rewrite the functions to single expression functions if possible.

The `addToBasket()` function can still be improved. What if we are not able to find the product for the given productId? The converted code will throw a Kotlin NullPointException because of the !! in `productById!!.listPrice`. A potential fix would be to properly check if we got result from `productRepository.getProductById(orderItem.productId)`.

**Exercise**: Add a null check for non existing products and throw an RuntimeException if not found.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
    @PostMapping(path = arrayOf("/baskets/{id}/items"), consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addToBasket(@PathVariable("id") id: String, @RequestBody orderItem: OrderItem): Basket {
        val productById = productRepository.getProductById(orderItem.productId) ?:
                throw RuntimeException("Product with productId: ${orderItem.productId} not found!")
        val basket = basketRepository.getBasketById(id)
        basket.addOrderItem(OrderItem(orderItem.productId, orderItem.quantity, productById.listPrice))
        return basket
    }
```
</details>
<br>

**Exercise**: We could polish the addToBasket function a bit more by removing the need to define the intermediate `val basket`. We can do this by directly calling the the [apply](https://dzone.com/articles/examining-kotlins-also-apply-let-run-and-with-intentions) function on `basketRepository.getBasketById(id)`. It would also be better to invoke `productRepository.getProductById()` inside the apply {} instead of before.

<details>
<summary>Suggested solution:</summary>

```kotlin
    @PostMapping(path = ["/baskets/{id}/items"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun addToBasket(@PathVariable id: String, @RequestBody orderItem: OrderItem): Basket {
        return basketRepository.getBasketById(id).apply {
            val product = productRepository.getProductById(orderItem.productId)
                    ?: throw RuntimeException("Product with productId: ${orderItem.productId} not found!")
            addOrderItem(OrderItem(orderItem.productId, orderItem.quantity, product.listPrice))
        }
    }
```
</details>
<br>

**Exercise**: Build and test your application if it is still working as expected.

### Next steps

You have now successfully converted all of the Java code to Kotlin! 

Continue with [exercise-4](exercise-4.md):

You can either start fresh by switching to the exercise-4 branch or continue on your current branch.

Switching to the exercise-4 branch can be done using IntelliJ or in your terminal by issuing the following command:

```
git checkout exercise-4
```