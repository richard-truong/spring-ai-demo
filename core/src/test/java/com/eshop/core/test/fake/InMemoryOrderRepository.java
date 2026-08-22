package com.eshop.core.test.fake;

import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.domain.model.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepositoryPort {

    private final List<Order> store = new ArrayList<>();

    @Override
    public Order save(Order order) {
        store.add(order);
        return order;
    }

    @Override
    public Optional<Order> findById(String id) {
        return store.stream().filter(order -> order.id().equals(id)).findFirst();
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return store.stream().filter(order -> order.userId().equals(userId)).toList();
    }

    public List<Order> saved() {
        return List.copyOf(store);
    }

}
