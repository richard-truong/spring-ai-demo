package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.JwtAuthFilter;
import com.eshop.app.adapter.in.security.SecurityConfig;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.core.application.dto.OrderItemResult;
import com.eshop.core.application.dto.OrderResult;
import com.eshop.core.application.port.in.PurchaseUseCase;
import com.eshop.core.domain.exception.InsufficientStockException;
import com.eshop.core.domain.exception.ProductNotFoundException;
import com.eshop.core.domain.vo.Money;
import com.eshop.core.domain.vo.OrderStatus;
import com.eshop.core.domain.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class})
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @MockitoBean
    PurchaseUseCase purchaseUseCase;

    @Test
    void purchaseWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void purchaseWithMalformedTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
            .andExpect(status().isUnauthorized());
    }


    @Test
    void purchaseWithValidTokenReturns201() throws Exception {        when(purchaseUseCase.purchase(any())).thenReturn(sampleOrderResult());

        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"p1\",\"quantity\":2}]}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("order-1"))
            .andExpect(jsonPath("$.userId").value("user-1"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.total").value(25.0));
    }

    @Test
    void purchaseEmptyItemsReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[]}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void purchaseUnknownProductReturns404() throws Exception {
        when(purchaseUseCase.purchase(any())).thenThrow(new ProductNotFoundException("missing"));

        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"missing\",\"quantity\":1}]}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void purchaseInsufficientStockReturns409() throws Exception {
        when(purchaseUseCase.purchase(any())).thenThrow(new InsufficientStockException("p1"));

        mockMvc.perform(post("/api/v1/orders")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"items\":[{\"productId\":\"p1\",\"quantity\":99}]}"))
            .andExpect(status().isConflict());
    }

    private String bearerToken() {
        return "Bearer " + tokenProvider.issue("user-1", "alice@example.com", Role.CUSTOMER).accessToken();
    }

    private OrderResult sampleOrderResult() {
        return new OrderResult(
            "order-1",
            "user-1",
            List.of(new OrderItemResult(
                "p1", "Espresso", 2, new Money("12.50", "USD"), new Money("25.00", "USD"))),
            new Money("25.00", "USD"),
            OrderStatus.PENDING,
            Instant.parse("2026-08-18T10:00:00Z")
        );
    }

}
