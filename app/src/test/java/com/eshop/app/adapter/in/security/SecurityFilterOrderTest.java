package com.eshop.app.adapter.in.security;

import com.eshop.app.adapter.in.web.ProductQueryController;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.app.infrastructure.security.RateLimitFilter;
import com.eshop.app.infrastructure.security.RateLimitProperties;
import com.eshop.core.application.port.in.ProductQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(ProductQueryController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class,
    RateLimitFilter.class, RateLimitProperties.class})
class SecurityFilterOrderTest {

    @Autowired
    FilterChainProxy filterChainProxy;

    @MockitoBean
    ProductQueryUseCase productQueryUseCase;

    @Test
    void jwtAuthFilterRunsBeforeRateLimitFilter() {
        List<SecurityFilterChain> chains = filterChainProxy.getFilterChains();
        List<jakarta.servlet.Filter> filters = chains.get(0).getFilters();

        int jwtIndex = indexOf(filters, JwtAuthFilter.class);
        int rateLimitIndex = indexOf(filters, RateLimitFilter.class);

        assertThat(jwtIndex).isGreaterThanOrEqualTo(0);
        assertThat(rateLimitIndex).isGreaterThanOrEqualTo(0);
        assertThat(jwtIndex).isLessThan(rateLimitIndex);
    }

    private int indexOf(List<jakarta.servlet.Filter> filters, Class<?> type) {
        for (int i = 0; i < filters.size(); i++) {
            if (type.isInstance(filters.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
