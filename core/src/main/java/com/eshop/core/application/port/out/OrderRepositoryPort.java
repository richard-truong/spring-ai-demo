package com.eshop.core.application.port.out;

import com.eshop.core.domain.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(String id);

    List<Order> findByUserId(String userId);

}
