package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.web.dto.ProductSuggestionRequest;
import com.eshop.app.adapter.in.web.dto.ProductSuggestionResponse;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.in.ProductSuggestionUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/suggest")
public class


ProductSuggestionController {

    private final ProductSuggestionUseCase productSuggestionUseCase;

    public ProductSuggestionController(ProductSuggestionUseCase productSuggestionUseCase) {
        this.productSuggestionUseCase = productSuggestionUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductSuggestionResponse> suggest(@Valid @RequestBody ProductSuggestionRequest request) {
        ProductSuggestionCommand command = new ProductSuggestionCommand(request.productName(), request.platform());
        return ResponseEntity.ok(ProductSuggestionResponse.from(productSuggestionUseCase.suggest(command)));
    }

}
