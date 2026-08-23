package com.eshop.core.application.usecase;

import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductQueryUseCaseTest {

    private final Product widget = new Product("p1", "Widget", "A widget", new Money("9.99", "USD"), 10);

    @Test
    void getByIdReturnsProductFromRepository() {
        RecordingProductRepository repository = new RecordingProductRepository();
        repository.findByIdResult = Optional.of(widget);
        ProductQueryUseCaseImpl useCase = new ProductQueryUseCaseImpl(repository);

        Product result = useCase.getById("p1");

        assertThat(result).isEqualTo(widget);
        assertThat(repository.findId).isEqualTo("p1");
    }

    @Test
    void getByIdThrowsWhenProductIsMissing() {
        RecordingProductRepository repository = new RecordingProductRepository();
        repository.findByIdResult = Optional.empty();
        ProductQueryUseCaseImpl useCase = new ProductQueryUseCaseImpl(repository);

        assertThatThrownBy(() -> useCase.getById("p1"))
            .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void findAllReturnsAllProducts() {
        RecordingProductRepository repository = new RecordingProductRepository();
        repository.findAllResult = List.of(widget);
        ProductQueryUseCaseImpl useCase = new ProductQueryUseCaseImpl(repository);

        assertThat(useCase.findAll()).containsExactly(widget);
    }

    private static final class RecordingProductRepository implements ProductRepositoryPort {

        Optional<Product> findByIdResult = Optional.empty();
        String findId;
        List<Product> findAllResult = List.of();

        @Override
        public Optional<Product> findById(String id) {
            this.findId = id;
            return findByIdResult;
        }

        @Override
        public List<Product> findAll() {
            return findAllResult;
        }

        @Override
        public List<Product> findAllById(List<String> ids) {
            return List.of();
        }

        @Override
        public Product save(Product product) {
            return product;
        }

        @Override
        public boolean decrementStock(String productId, int quantity) {
            return false;
        }

    }

}
