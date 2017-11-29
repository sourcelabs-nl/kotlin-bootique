package com.bootique.bootique;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class OrderItem {

    private String productId;
    private int quantity;
    private BigDecimal price;

    @JsonCreator
    public OrderItem(@JsonProperty("productId") String productId, @JsonProperty("quantity") int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
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

    public BigDecimal getTotalPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}
