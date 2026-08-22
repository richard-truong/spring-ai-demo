package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.AuthenticatedUser;
import com.eshop.app.adapter.in.web.dto.OrderResponse;
import com.eshop.app.adapter.in.web.dto.PurchaseRequest;
import com.eshop.core.application.dto.PurchaseCommand;
import com.eshop.core.application.dto.PurchaseItem;
import com.eshop.core.application.port.in.PurchaseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final PurchaseUseCase purchaseUseCase;

    public OrderController(PurchaseUseCase purchaseUseCase) {
        this.purchaseUseCase = purchaseUseCase;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> purchase(@Valid @RequestBody PurchaseRequest request,
                                                  Authentication authentication) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        List<PurchaseItem> items = request.items().stream()
            .map(item -> new PurchaseItem(item.productId(), item.quantity()))
            .toList();
        PurchaseCommand command = new PurchaseCommand(principal.id(), items);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(purchaseUseCase.purchase(command)));
    }

}
