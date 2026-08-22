package com.eshop.core.application.port.out;

import com.eshop.core.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {

    Optional<Product> findById(String id);

    List<Product> findAllById(List<String> ids);

    Product save(Product product);

    boolean decrementStock(String productId, int quantity);

}
