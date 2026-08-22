package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.OrderResult;
import com.eshop.core.application.dto.PurchaseCommand;
import com.eshop.core.application.dto.PurchaseItem;
import com.eshop.core.application.port.in.PurchaseUseCase;
import com.eshop.core.domain.exception.CurrencyMismatchException;
import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.EmptyOrderException;
import com.eshop.core.domain.exception.InsufficientStockException;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;
import com.eshop.core.test.fake.InMemoryOrderRepository;
import com.eshop.core.test.fake.InMemoryProductRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseUseCaseTest {

    private final InMemoryProductRepository productRepository = new InMemoryProductRepository();
    private final InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
    private final PurchaseUseCase useCase = new PurchaseUseCaseImpl(
        orderRepository,
        productRepository,
        () -> "order-1",
        () -> Instant.parse("2026-08-18T10:00:00Z")
    );

    @Test
    void placesOrderAndDecreasesStock() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5),
            new Product("p2", "Latte", "Milk coffee", new Money("4.50", "USD"), 10)
        );

        OrderResult result = useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 2),
            new PurchaseItem("p2", 1)
        )));

        assertThat(result.total().amount()).isEqualByComparingTo("29.50");
        assertThat(result.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.items()).hasSize(2);

        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(3);
        assertThat(productRepository.findById("p2").orElseThrow().stock()).isEqualTo(9);
        assertThat(orderRepository.saved()).hasSize(1);
    }

    @Test
    void rejectsUnknownProduct() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("missing", 1)
        )))).isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void rejectsInsufficientStockAndPersistsNothing() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 1)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 2)
        )))).isInstanceOf(InsufficientStockException.class);

        assertThat(orderRepository.saved()).isEmpty();
        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(1);
    }

    @Test
    void rejectsEmptyItems() {
        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of())))
            .isInstanceOf(EmptyOrderException.class);
    }

    @Test
    void rejectsNonPositiveQuantityWithoutTouchingStock() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 0)
        )))).isInstanceOf(DomainException.class);

        assertThat(orderRepository.saved()).isEmpty();
        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(5);
    }

    @Test
    void validFirstItemThenInvalidQuantityLeavesAllStockUnchanged() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5),
            new Product("p2", "Latte", "Milk coffee", new Money("4.50", "USD"), 10)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 2),
            new PurchaseItem("p2", 0)
        )))).isInstanceOf(DomainException.class);

        assertThat(orderRepository.saved()).isEmpty();
        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(5);
        assertThat(productRepository.findById("p2").orElseThrow().stock()).isEqualTo(10);
    }

    @Test
    void validFirstItemThenOutOfStockLeavesAllStockUnchanged() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5),
            new Product("p2", "Latte", "Milk coffee", new Money("4.50", "USD"), 1)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 2),
            new PurchaseItem("p2", 99)
        )))).isInstanceOf(InsufficientStockException.class);

        assertThat(orderRepository.saved()).isEmpty();
        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(5);
        assertThat(productRepository.findById("p2").orElseThrow().stock()).isEqualTo(1);
    }

    @Test
    void rejectsMixedCurrencyOrderWithoutTouchingStock() {
        productRepository.seed(
            new Product("p1", "Espresso", "Single origin", new Money("12.50", "USD"), 5),
            new Product("p2", "Croissant", "Buttery", new Money("3.75", "EUR"), 10)
        );

        assertThatThrownBy(() -> useCase.purchase(new PurchaseCommand("user-1", List.of(
            new PurchaseItem("p1", 1),
            new PurchaseItem("p2", 1)
        )))).isInstanceOf(CurrencyMismatchException.class);

        assertThat(orderRepository.saved()).isEmpty();
        assertThat(productRepository.findById("p1").orElseThrow().stock()).isEqualTo(5);
        assertThat(productRepository.findById("p2").orElseThrow().stock()).isEqualTo(10);
    }

}
