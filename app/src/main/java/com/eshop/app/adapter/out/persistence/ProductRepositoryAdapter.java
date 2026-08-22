package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.ProductEntity;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository repository;

    public ProductRepositoryAdapter(ProductJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Product> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Product> findAllById(List<String> ids) {
        return repository.findAllById(ids).stream().map(this::toDomain).toList();
    }

    @Override
    public Product save(Product product) {
        return toDomain(repository.save(toEntity(product)));
    }

    @Override
    public boolean decrementStock(String productId, int quantity) {
        return repository.decrementStock(productId, quantity) > 0;
    }

    private ProductEntity toEntity(Product product) {
        return new ProductEntity(
            product.id(),
            product.name(),
            product.description(),
            product.price().amount(),
            product.price().currency(),
            product.stock()
        );
    }

    private Product toDomain(ProductEntity entity) {
        return new Product(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            new Money(entity.getPriceAmount(), entity.getPriceCurrency()),
            entity.getStock()
        );
    }

}
