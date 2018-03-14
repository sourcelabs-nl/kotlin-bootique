## Exercise 3: more conversion fun

### Convert Basket.java to Kotlin

Open Basket.java

**Exercise**: convert Basket.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

After the conversion the code is broken, just take a look at the totalPrice calculation. Apparently IntelliJ is not able to figure out all the Java Stream API operations and tries to make the best out of it. 

We can fix this by writing the calculation in exactly the same way as we would do with the Java Stream API:

```kotlin
val totalPrice: BigDecimal        
    get() = items.stream().map(OrderItem::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
```

Kotlin has a richer (functional) API which allows us write this in a different way without using the Java Stream API, for example we could achieve the same using the fold function:

```kotlin
val totalPrice: BigDecimal 
    get() = items.fold(BigDecimal.ZERO, { sum, item -> sum.plus(item.totalPrice) })
```

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

**Exercise**: write an extension function which allows for summing BigDecimals. Hint: look at the implementation of `public inline fun <T> Iterable<T>.sumBy(selector: (T) -> Int): Int`

<details>
  <summary>The resulting code should look like this:</summary>
  
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

**Exercise**: Try to write the totalPrice calculation as a single expression function called getTotalPrice().

<details>
  <summary>The resulting code should look like this:</summary>

```kotlin
fun getTotalPrice() = items.sumBy { it.totalPrice }
```
</details>
<br>

### Convert BasketRepository.java to Kotlin

Open BasketRepository.java

**Exercise**: convert BasketRepository.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

Two interesting things about the converted code. First have a look at the getBasketById(), there is some explicit casting into a Map.

**Exercise**: remove the cast to java.util.Map<String, Basket>

See if you can find a method in the Kotlin map which would be a nice replacement for computeIfAbsent

**Exercise**: replace computeIfAbsent by a more concise Kotlin alternative

<details>
  <summary>The resulting code should look like this:</summary>

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

Open ProductRepository.java

**Exercise**: convert ProductRepository.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

You will notice that getProductById() is broken, this is because the expected return can actually be null but the method defines a NonNullable return type: Product. The implementation might return a null value when the product cannot be found.

**Exercise**: change the return type of `fun getProductById()` that it allows for a returning nullable Product.

<details>
<summary>The resulting code should look like this:</summary>

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
<summary>The resulting code should look like this:</summary>

```kotlin
fun getProductById(productId: String) = products[productId]
```
</details>
<br>

The implementation of `fun getProducts(): List<Product>` was translated from Java, but we can improve this the Kotlin way. The idea behind the Java implementation was to always return an immutable List of Products so that it can never be modified outside of the class. In Kotlin we can return the products.values and convert that to a List, which is immutable by default.

**Exercise**: Change `fun getProducts(): List<Product>` in a way that it returns products.values as a Kotlin (immutable) List 

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
fun getProducts() = products.values.toList()
```
</details>
<br>

### Convert BootiqueController.java to Kotlin

Open BootiqueController.java

**Exercise**: convert BootiqueController.java to Kotlin using IntelliJ (menu > Code > Convert Java File to Kotlin File). 

The resulting code looks pretty ok.

**Exercise**: Rewrite the functions to single expression functions if possible.

The addToBasket() function can still be improved. What if we are not able to find the product for the given productId? The converted code will throw a Kotlin NullPointException because of the !! in `productById!!.listPrice`. A potential fix would be to properly check if we got result from `productRepository.getProductById(orderItem.productId)`.

**Exercise**: Add a null check for non existing products and throw an IllegalArgumentException if not found.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
    @PostMapping(path = arrayOf("/baskets/{id}/items"), consumes = arrayOf(MediaType.APPLICATION_JSON_UTF8_VALUE))
    fun addToBasket(@PathVariable("id") id: String, @RequestBody orderItem: OrderItem): Basket {
        val productById = productRepository.getProductById(orderItem.productId) ?:
                throw IllegalArgumentException("Product with productId: ${orderItem.productId} not found!")
        val basket = basketRepository.getBasketById(id)
        basket.addOrderItem(OrderItem(orderItem.productId, orderItem.quantity, productById.listPrice))
        return basket
    }
```
</details>
<br>

A frequently heard complaint in previous versions of Kotlin is the necessity to to wrap Array[] properties of an annotation with arrayOf(). Since Kotlin 1.2 you can use the [] block notation instead of the arrayOf() syntax.

**Exercise**: Replace the arrayOf() by [] in the BootiqueController @Annotations where possible.

**Bonus**: We could polish the addToBasket function a bit more by removing the need to define the intermediate `val basket`. We can do this by directly calling the the [apply](https://dzone.com/articles/examining-kotlins-also-apply-let-run-and-with-intentions) function on `basketRepository.getBasketById(id)`. It would also be better to invoke `productRepository.getProductById()` inside the apply {} instead of before.

<details>
<summary>The resulting code should look like this:</summary>

```kotlin
    @PostMapping(path = ["/baskets/{id}/items"], consumes = [MediaType.APPLICATION_JSON_UTF8_VALUE])
    fun addToBasket(@PathVariable id: String, @RequestBody orderItem: OrderItem): Basket {
        return basketRepository.getBasketById(id).apply {
            val product = productRepository.getProductById(orderItem.productId)
                    ?: throw IllegalArgumentException("Product with productId: ${orderItem.productId} not found!")
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
