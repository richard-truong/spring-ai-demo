package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.JwtAuthFilter;
import com.eshop.app.adapter.in.security.SecurityConfig;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.port.in.ProductSuggestionUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductSuggestionController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class})
class ProductSuggestionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ProductSuggestionUseCase productSuggestionUseCase;

    @Test
    void suggestReturnsProduct() throws Exception {
        when(productSuggestionUseCase.suggest(any()))
            .thenReturn(new ProductSuggestion("Laptop Gaming X1", "1.999.000đ", "RTX 4060, 16GB RAM"));

        mockMvc.perform(post("/api/v1/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productName\":\"Laptop\",\"platform\":\"shopee\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop Gaming X1"))
            .andExpect(jsonPath("$.price").value("1.999.000đ"))
            .andExpect(jsonPath("$.description").value("RTX 4060, 16GB RAM"));
    }

    @Test
    void suggestValidationErrorReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"productName\":\"\",\"platform\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

}
