package com.bootique.bootique;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Basket contains the order items for a specific user or session.
 */
public class Basket {
    private final List<OrderItem> items = new CopyOnWriteArrayList<>();

    public List<OrderItem> getOrderItems() {
        return Collections.unmodifiableList(items);
    }

    public void addOrderItem(OrderItem orderItem) {
        items.add(orderItem);
    }

    /**
     * Calculates the sum of the order item totalPrice.
     * @return BigDecimal.ZERO in case of an empty basket.
     */
    public BigDecimal getTotalPrice() {
        return items.stream().map(OrderItem::getTotalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
