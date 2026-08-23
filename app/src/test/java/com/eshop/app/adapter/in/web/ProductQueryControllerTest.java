package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.JwtAuthFilter;
import com.eshop.app.adapter.in.security.SecurityConfig;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.app.infrastructure.security.RateLimitFilter;
import com.eshop.app.infrastructure.security.RateLimitProperties;
import com.eshop.core.application.port.in.ProductQueryUseCase;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.model.Product;
import com.eshop.core.domain.vo.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductQueryController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class,
    RateLimitFilter.class, RateLimitProperties.class})
class ProductQueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductQueryUseCase productQueryUseCase;

    @Test
    void findAllReturnsProducts() throws Exception {
        when(productQueryUseCase.findAll()).thenReturn(List.of(
            new Product("p1", "Widget", "A widget", new Money("9.99", "USD"), 10)
        ));

        mockMvc.perform(get("/api/v1/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value("p1"))
            .andExpect(jsonPath("$[0].name").value("Widget"))
            .andExpect(jsonPath("$[0].price.amount").value(9.99))
            .andExpect(jsonPath("$[0].price.currency").value("USD"))
            .andExpect(jsonPath("$[0].stock").value(10));
    }

    @Test
    void findByIdReturnsProduct() throws Exception {
        when(productQueryUseCase.getById("p1"))
            .thenReturn(new Product("p1", "Widget", "A widget", new Money("9.99", "USD"), 10));

        mockMvc.perform(get("/api/v1/products/p1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("p1"))
            .andExpect(jsonPath("$.price.currency").value("USD"));
    }

    @Test
    void findByIdReturns404WhenProductIsMissing() throws Exception {
        when(productQueryUseCase.getById("missing"))
            .thenThrow(new ProductNotFoundException("missing"));

        mockMvc.perform(get("/api/v1/products/missing"))
            .andExpect(status().isNotFound());
    }

}
