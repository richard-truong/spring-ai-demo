package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.ProductEntity;
import com.eshop.app.config.CacheConfig;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductRepositoryCacheTest {

    private final ProductEntity widgetEntity =
        new ProductEntity("p1", "Widget", "A widget", new BigDecimal("9.99"), "USD", 10);

    @Test
    void findByIdHitsRepositoryOnceThenServesFromCache() {
        ProductJpaRepository jpaRepository = mock(ProductJpaRepository.class);
        when(jpaRepository.findById("p1")).thenReturn(Optional.of(widgetEntity));

        try (AnnotationConfigApplicationContext context = buildContext(jpaRepository)) {
            ProductRepositoryPort adapter = context.getBean(ProductRepositoryPort.class);

            Product first = adapter.findById("p1").orElseThrow();
            adapter.findById("p1");

            verify(jpaRepository, times(1)).findById("p1");
            assertThat(first.name()).isEqualTo("Widget");
            assertThat(first.price()).isEqualTo(new Money("9.99", "USD"));
        }
    }

    @Test
    void findAllIsCached() {
        ProductJpaRepository jpaRepository = mock(ProductJpaRepository.class);
        when(jpaRepository.findAll()).thenReturn(List.of(widgetEntity));

        try (AnnotationConfigApplicationContext context = buildContext(jpaRepository)) {
            ProductRepositoryPort adapter = context.getBean(ProductRepositoryPort.class);

            adapter.findAll();
            adapter.findAll();

            verify(jpaRepository, times(1)).findAll();
        }
    }

    @Test
    void decrementStockEvictsCachedProduct() {
        ProductJpaRepository jpaRepository = mock(ProductJpaRepository.class);
        when(jpaRepository.findById("p1")).thenReturn(Optional.of(widgetEntity));
        when(jpaRepository.decrementStock("p1", 2)).thenReturn(1);

        try (AnnotationConfigApplicationContext context = buildContext(jpaRepository)) {
            ProductRepositoryPort adapter = context.getBean(ProductRepositoryPort.class);

            adapter.findById("p1");
            adapter.decrementStock("p1", 2);
            adapter.findById("p1");

            verify(jpaRepository, times(2)).findById("p1");
        }
    }

    private AnnotationConfigApplicationContext buildContext(ProductJpaRepository jpaRepository) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(CacheConfig.class);
        context.getBeanFactory().registerSingleton("jpaRepository", jpaRepository);
        context.register(ProductRepositoryAdapter.class);
        context.refresh();
        return context;
    }

}
