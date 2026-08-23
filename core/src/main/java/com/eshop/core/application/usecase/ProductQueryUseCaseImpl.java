package com.eshop.core.application.usecase;

import com.eshop.core.application.port.in.ProductQueryUseCase;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.model.Product;

import java.util.List;

public class ProductQueryUseCaseImpl implements ProductQueryUseCase {

    private final ProductRepositoryPort productRepository;

    public ProductQueryUseCaseImpl(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getById(String id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

}
