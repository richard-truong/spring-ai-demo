package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.OrderItemResult;
import com.eshop.core.application.dto.OrderResult;
import com.eshop.core.application.dto.PurchaseCommand;
import com.eshop.core.application.dto.PurchaseItem;
import com.eshop.core.application.port.in.PurchaseUseCase;
import com.eshop.core.application.port.out.ClockPort;
import com.eshop.core.application.port.out.IdGeneratorPort;
import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.exception.CurrencyMismatchException;
import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.EmptyOrderException;
import com.eshop.core.domain.exception.InsufficientStockException;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.model.Order;
import com.eshop.core.domain.model.OrderItem;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PurchaseUseCaseImpl implements PurchaseUseCase {

    private final OrderRepositoryPort orderRepository;
    private final ProductRepositoryPort productRepository;
    private final IdGeneratorPort idGenerator;
    private final ClockPort clock;

    public PurchaseUseCaseImpl(OrderRepositoryPort orderRepository,
                               ProductRepositoryPort productRepository,
                               IdGeneratorPort idGenerator,
                               ClockPort clock) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    @Override
    public OrderResult purchase(PurchaseCommand command) {
        List<PurchaseItem> purchaseItems = command.items();
        if (purchaseItems == null || purchaseItems.isEmpty()) {
            throw new EmptyOrderException();
        }
        if (command.userId() == null || command.userId().isBlank()) {
            throw new DomainException("userId must not be blank");
        }

        for (PurchaseItem item : purchaseItems) {
            validateItem(item);
        }

        Map<String, Integer> requestedQuantities = purchaseItems.stream()
            .collect(Collectors.toMap(PurchaseItem::productId, PurchaseItem::quantity, Integer::sum));

        Map<String, Product> byId = productRepository.findAllById(new ArrayList<>(requestedQuantities.keySet())).stream()
            .collect(Collectors.toMap(Product::id, Function.identity()));

        if (byId.size() != requestedQuantities.size()) {
            String missing = requestedQuantities.keySet().stream()
                .filter(id -> !byId.containsKey(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(requestedQuantities.keySet().iterator().next()));
            throw new ProductNotFoundException(missing);
        }

        validateSingleCurrency(byId.values().stream().map(Product::price).toList());

        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            Product product = byId.get(entry.getKey());
            if (product.stock() < entry.getValue()) {
                throw new InsufficientStockException(entry.getKey());
            }
        }

        for (Map.Entry<String, Integer> entry : requestedQuantities.entrySet()) {
            if (!productRepository.decrementStock(entry.getKey(), entry.getValue())) {
                throw new InsufficientStockException(entry.getKey());
            }
        }

        List<OrderItem> orderItems = purchaseItems.stream()
            .map(item -> {
                Product product = byId.get(item.productId());
                return new OrderItem(product.id(), product.name(), item.quantity(), product.price());
            })
            .toList();

        Order order = new Order(
            idGenerator.nextId(),
            command.userId(),
            orderItems,
            OrderStatus.PENDING,
            clock.now()
        );

        Order saved = orderRepository.save(order);
        return toResult(saved);
    }

    private void validateItem(PurchaseItem item) {
        if (item == null) {
            throw new DomainException("purchase item must not be null");
        }
        if (item.productId() == null || item.productId().isBlank()) {
            throw new DomainException("productId must not be blank");
        }
        if (item.quantity() <= 0) {
            throw new DomainException("quantity must be positive");
        }
    }

    private void validateSingleCurrency(List<Money> prices) {
        String currency = null;
        for (Money price : prices) {
            if (currency == null) {
                currency = price.currency();
            } else if (!currency.equals(price.currency())) {
                throw new CurrencyMismatchException();
            }
        }
    }

    private OrderResult toResult(Order order) {
        List<OrderItemResult> items = order.items().stream()
            .map(item -> new OrderItemResult(
                item.productId(),
                item.name(),
                item.quantity(),
                item.unitPrice(),
                item.subtotal()
            ))
            .toList();
        return new OrderResult(
            order.id(),
            order.userId(),
            items,
            order.total(),
            order.status(),
            order.createdAt()
        );
    }

}
