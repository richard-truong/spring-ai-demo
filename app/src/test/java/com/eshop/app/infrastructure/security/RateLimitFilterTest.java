package com.eshop.app.infrastructure.security;

import com.eshop.app.adapter.in.security.AuthenticatedUser;
import com.eshop.core.domain.vo.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitFilterTest {

    private RateLimitFilter filter;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        RateLimitProperties properties = new RateLimitProperties();
        properties.setRules(List.of(
            new RateLimitProperties.Rule(
                "/api/v1/auth/**",
                new RateLimitProperties.Limit(5, 1, 60))
        ));
        filter = new RateLimitFilter(properties);
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsRequestsWithinDefaultCapacity() throws Exception {
        for (int i = 0; i < 60; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(anonymousRequest("/api/v1/orders"), response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
        assertThat(response.getHeader(RateLimitFilter.HEADER_LIMIT)).isEqualTo("60");
        assertThat(response.getHeader(RateLimitFilter.HEADER_REMAINING)).isEqualTo("0");
    }

    @Test
    void rejectsRequestBeyondDefaultCapacity() throws Exception {
        for (int i = 0; i < 60; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(anonymousRequest("/api/v1/orders"), response, new MockFilterChain());
        }
        filter.doFilter(anonymousRequest("/api/v1/orders"), response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader(RateLimitFilter.HEADER_RETRY_AFTER)).isNotBlank();
        assertThat(response.getContentType()).isEqualTo("application/problem+json");
    }

    @Test
    void authRuleAppliesStricterLimit() throws Exception {
        for (int i = 0; i < 5; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(anonymousRequest("/api/v1/auth/login"), response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
        filter.doFilter(anonymousRequest("/api/v1/auth/login"), response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getHeader(RateLimitFilter.HEADER_LIMIT)).isEqualTo("5");
    }

    @Test
    void authenticatedUserGetsSeparateBucketFromIp() throws Exception {
        var authentication = new UsernamePasswordAuthenticationToken(
            new AuthenticatedUser("user-1", "alice@example.com", Role.CUSTOMER), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        for (int i = 0; i < 60; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(authenticatedRequest("/api/v1/orders"), response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
        filter.doFilter(authenticatedRequest("/api/v1/orders"), response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(429);

        SecurityContextHolder.clearContext();
        response = new MockHttpServletResponse();
        filter.doFilter(anonymousRequest("/api/v1/orders"), response, new MockFilterChain());
        assertThat(response.getStatus()).isEqualTo(200);
    }

    private MockHttpServletRequest anonymousRequest(String uri) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setRemoteAddr("192.168.1.10");
        return request;
    }

    private MockHttpServletRequest authenticatedRequest(String uri) {
        MockHttpServletRequest request = anonymousRequest(uri);
        request.setRemoteAddr("192.168.1.10");
        return request;
    }

}
