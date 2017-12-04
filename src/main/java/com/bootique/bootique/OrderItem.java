package com.bootique.bootique;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents the product, quantity and price of an item in the Basket.
 */
public class OrderItem {

    private final String productId;
    private final int quantity;
    private final BigDecimal price;

    @JsonCreator
    public OrderItem(@JsonProperty("productId") String productId, @JsonProperty("quantity") int quantity) {
        this(productId, quantity, BigDecimal.ZERO);
    }

    public OrderItem(String productId, int quantity, BigDecimal price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Calculates the totalPrice of this item: price * quantity
     *
     * @return BigDecimal.ZERO if no price is define
     */
    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}
