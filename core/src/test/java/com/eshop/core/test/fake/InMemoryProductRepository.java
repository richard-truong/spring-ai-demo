package com.eshop.core.test.fake;

import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepositoryPort {

    private final Map<String, Product> store = new HashMap<>();

    public void seed(Product... products) {
        for (Product product : products) {
            store.put(product.id(), product);
        }
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAllById(List<String> ids) {
        return ids.stream().map(store::get).filter(Objects::nonNull).toList();
    }

    @Override
    public Product save(Product product) {
        store.put(product.id(), product);
        return product;
    }

    @Override
    public boolean decrementStock(String productId, int quantity) {
        Product product = store.get(productId);
        if (product == null || product.stock() < quantity) {
            return false;
        }
        store.put(productId, new Product(
            product.id(),
            product.name(),
            product.description(),
            product.price(),
            product.stock() - quantity
        ));
        return true;
    }

}
