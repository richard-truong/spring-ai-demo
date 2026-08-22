package com.eshop.core.domain.model;

import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.EmptyOrderException;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void totalIsDerivedFromItemSubtotals() {
        OrderItem espresso = new OrderItem("p1", "Espresso", 2, new Money("12.50", "USD"));
        OrderItem latte = new OrderItem("p2", "Latte", 1, new Money("4.50", "USD"));

        Order order = new Order("o1", "u1", List.of(espresso, latte), OrderStatus.PENDING, Instant.now());

        assertThat(order.total().amount()).isEqualByComparingTo("29.50");
        assertThat(espresso.subtotal().amount()).isEqualByComparingTo("25.00");
    }

    @Test
    void rejectsEmptyItems() {
        assertThatThrownBy(() -> new Order("o1", "u1", List.of(), OrderStatus.PENDING, Instant.now()))
            .isInstanceOf(EmptyOrderException.class);
    }

    @Test
    void rejectsNullItems() {
        assertThatThrownBy(() -> new Order("o1", "u1", null, OrderStatus.PENDING, Instant.now()))
            .isInstanceOf(EmptyOrderException.class);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> new OrderItem("p1", "Espresso", 0, new Money("12.50", "USD")))
            .isInstanceOf(DomainException.class);
    }

}
