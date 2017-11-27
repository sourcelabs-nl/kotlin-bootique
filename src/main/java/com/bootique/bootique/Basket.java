package com.bootique.bootique;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Basket {
    private List<OrderItem> items = new ArrayList<>();

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(items);
    }

    public void addOrderItem(OrderItem orderItem) {
        items.add(orderItem);
    }

    public BigDecimal getTotalPrice() {
        return items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
