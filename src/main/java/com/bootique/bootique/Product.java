package com.bootique.bootique;

import java.math.BigDecimal;

/**
 * A product that can be bought.
 */
public class Product {

    private final String id;
    private final String title;
    private final String brand;
    private final BigDecimal listPrice;

    public Product(String id, String title, String brand, BigDecimal listPrice) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.listPrice = listPrice;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBrand() {
        return brand;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }
}
