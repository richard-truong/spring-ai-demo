package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.web.dto.ProductResponse;
import com.eshop.core.application.port.in.ProductQueryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductQueryController {

    private final ProductQueryUseCase productQueryUseCase;

    public ProductQueryController(ProductQueryUseCase productQueryUseCase) {
        this.productQueryUseCase = productQueryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(
            productQueryUseCase.findAll().stream().map(ProductResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable String id) {
        return ResponseEntity.ok(ProductResponse.from(productQueryUseCase.getById(id)));
    }

}
