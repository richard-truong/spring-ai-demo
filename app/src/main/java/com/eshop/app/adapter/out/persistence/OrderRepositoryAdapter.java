package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.OrderEntity;
import com.eshop.app.adapter.out.persistence.entity.OrderItemEntity;
import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.domain.model.Order;
import com.eshop.core.domain.model.OrderItem;
import com.eshop.core.domain.vo.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final OrderJpaRepository repository;

    public OrderRepositoryAdapter(OrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Order save(Order order) {
        return toDomain(repository.save(toEntity(order)));
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findByIdWithItems(id).map(this::toDomain);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    private OrderEntity toEntity(Order order) {
        Money total = order.total();
        OrderEntity entity = new OrderEntity(
            order.id(),
            order.userId(),
            order.status(),
            total.amount(),
            total.currency(),
            order.createdAt()
        );
        for (OrderItem item : order.items()) {
            Money subtotal = item.subtotal();
            entity.addItem(new OrderItemEntity(
                item.productId(),
                item.name(),
                item.quantity(),
                item.unitPrice().amount(),
                item.unitPrice().currency(),
                subtotal.amount(),
                subtotal.currency()
            ));
        }
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
            .map(item -> new OrderItem(
                item.getProductId(),
                item.getName(),
                item.getQuantity(),
                new Money(item.getUnitPriceAmount(), item.getUnitPriceCurrency())
            ))
            .toList();
        return new Order(
            entity.getId(),
            entity.getUserId(),
            items,
            entity.getStatus(),
            entity.getCreatedAt()
        );
    }

}
