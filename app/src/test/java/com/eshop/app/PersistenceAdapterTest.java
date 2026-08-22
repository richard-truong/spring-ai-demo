package com.eshop.app;

import com.eshop.app.support.PostgresIntegrationTest;
import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.model.Order;
import com.eshop.core.domain.model.OrderItem;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;
import com.eshop.core.domain.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceAdapterTest extends PostgresIntegrationTest {

    private static final Instant FIXED_NOW = Instant.parse("2026-08-18T10:00:00Z");

    @Autowired
    UserRepositoryPort userRepository;

    @Autowired
    ProductRepositoryPort productRepository;

    @Autowired
    OrderRepositoryPort orderRepository;

    @Test
    void userRoundTrip() {
        User user = new User(
            UUID.randomUUID().toString(),
            new Email("bob@example.com"),
            "Bob",
            "hashed-password",
            Role.CUSTOMER,
            FIXED_NOW
        );

        User saved = userRepository.save(user);

        assertThat(userRepository.findById(saved.id())).contains(saved);
        assertThat(userRepository.findByEmail(new Email("BOB@example.com"))).contains(saved);
        assertThat(userRepository.existsByEmail(new Email("bob@example.com"))).isTrue();
    }

    @Test
    void productAndOrderRoundTrip() {
        Product product = new Product(
            UUID.randomUUID().toString(),
            "Espresso",
            "Single origin",
            new Money("12.50", "USD"),
            5
        );
        productRepository.save(product);

        Order order = new Order(
            UUID.randomUUID().toString(),
            "user-1",
            List.of(new OrderItem(product.id(), product.name(), 2, product.price())),
            OrderStatus.PENDING,
            FIXED_NOW
        );

        Order saved = orderRepository.save(order);

        Order loaded = orderRepository.findById(saved.id()).orElseThrow();
        assertThat(loaded.total().amount()).isEqualByComparingTo("25.00");
        assertThat(loaded.items()).hasSize(1);
        assertThat(loaded.items().get(0).subtotal().amount()).isEqualByComparingTo("25.00");
        assertThat(orderRepository.findByUserId("user-1")).hasSize(1);
    }

    @Test
    void decrementStockIsAtomic() {
        Product product = new Product(
            UUID.randomUUID().toString(),
            "Espresso",
            "Single origin",
            new Money("12.50", "USD"),
            5
        );
        productRepository.save(product);

        assertThat(productRepository.decrementStock(product.id(), 3)).isTrue();
        assertThat(productRepository.findById(product.id()).orElseThrow().stock()).isEqualTo(2);

        assertThat(productRepository.decrementStock(product.id(), 3)).isFalse();
        assertThat(productRepository.findById(product.id()).orElseThrow().stock()).isEqualTo(2);
    }

}
