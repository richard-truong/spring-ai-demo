package com.eshop.core.application.port.in;

import com.eshop.core.domain.model.Product;

import java.util.List;

public interface ProductQueryUseCase {

    Product getById(String id);

    List<Product> findAll();

}
