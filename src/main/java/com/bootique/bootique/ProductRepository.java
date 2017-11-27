package com.bootique.bootique;

import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ProductRepository {

    private static final Map<String, Product> products = new ConcurrentHashMap<>();

    static {
        products.put("1", new Product("1", "iPhone X", "Apple", new BigDecimal("989.99")));
        products.put("2", new Product("2", "Galaxy S8", "Samsung", new BigDecimal("699.99")));
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(new ArrayList<>(products.values()));
    }

    public Product getProductById(String productId) {
        return products.get(productId);
    }
}
