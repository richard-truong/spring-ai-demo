package com.eshop.core.domain.model;

import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.InsufficientStockException;
import com.eshop.core.domain.vo.Money;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private final Product espresso =
        new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5);

    @Test
    void decreasesStock() {
        Product updated = espresso.decreaseStock(2);

        assertThat(updated.stock()).isEqualTo(3);
        assertThat(espresso.stock()).isEqualTo(5);
    }

    @Test
    void throwsWhenStockInsufficient() {
        assertThatThrownBy(() -> espresso.decreaseStock(6))
            .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void rejectsNonPositiveQuantity() {
        assertThatThrownBy(() -> espresso.decreaseStock(0))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNegativeStock() {
        assertThatThrownBy(() -> new Product("p1", "Espresso", "d", new Money("12.50", "USD"), -1))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsNegativePrice() {
        assertThatThrownBy(() -> new Product("p1", "Espresso", "d", new Money("-1.00", "USD"), 5))
            .isInstanceOf(DomainException.class);
    }

}
