package com.bootique.bootique;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BasketRepository {

    private static final Map<String, Basket> baskets = new ConcurrentHashMap<>();

    public Basket getBasketById(String id) {
        return baskets.computeIfAbsent(id, basket -> new Basket());
    }
}
