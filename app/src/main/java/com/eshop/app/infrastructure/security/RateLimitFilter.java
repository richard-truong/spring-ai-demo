package com.eshop.app.infrastructure.security;

import com.eshop.app.adapter.in.security.AuthenticatedUser;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    public static final String HEADER_LIMIT = "X-RateLimit-Limit";
    public static final String HEADER_REMAINING = "X-RateLimit-Remaining";
    public static final String HEADER_RESET = "X-RateLimit-Reset";
    public static final String HEADER_RETRY_AFTER = "Retry-After";

    private final RateLimitProperties properties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final com.github.benmanes.caffeine.cache.Cache<BucketKey, Bucket> buckets;

    public RateLimitFilter(RateLimitProperties properties) {
        this.properties = properties;
        this.buckets = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RateLimitProperties.Limit limit = resolveLimit(request.getRequestURI());
        Bucket bucket = buckets.get(bucketKey(request), key -> newBucket(limit));
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        applyHeaders(response, limit, probe);
        if (probe.isConsumed()) {
            filterChain.doFilter(request, response);
        } else {
            writeTooManyRequests(response, probe.getNanosToWaitForRefill());
        }
    }

    private RateLimitProperties.Limit resolveLimit(String path) {
        return properties.getRules().stream()
            .filter(rule -> pathMatcher.match(rule.pathPattern(), path))
            .map(RateLimitProperties.Rule::limit)
            .findFirst()
            .orElse(properties.getDefaultLimit());
    }

    private BucketKey bucketKey(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
            return new BucketKey("user", user.id());
        }
        return new BucketKey("ip", request.getRemoteAddr());
    }

    private Bucket newBucket(RateLimitProperties.Limit limit) {
        Duration period = Duration.ofSeconds(limit.refillPeriodSeconds());
        Bandwidth bandwidth = Bandwidth.builder()
            .capacity(limit.capacity())
            .refillGreedy(limit.refillTokens(), period)
            .build();
        return Bucket.builder().addLimit(bandwidth).build();
    }

    private void applyHeaders(HttpServletResponse response,
                              RateLimitProperties.Limit limit,
                              ConsumptionProbe probe) {
        response.setHeader(HEADER_LIMIT, String.valueOf(limit.capacity()));
        response.setHeader(HEADER_REMAINING, String.valueOf(Math.max(probe.getRemainingTokens(), 0)));
        response.setHeader(HEADER_RESET,
            String.valueOf(TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())));
    }

    private void writeTooManyRequests(HttpServletResponse response, long nanosToWait) throws IOException {
        response.setStatus(429);
        response.setHeader(HEADER_RETRY_AFTER,
            String.valueOf(Math.max(1, TimeUnit.NANOSECONDS.toSeconds(nanosToWait))));
        response.setContentType("application/problem+json");
        response.getWriter().write(
            "{\"type\":\"about:blank\",\"title\":\"Too Many Requests\",\"status\":429,"
                + "\"detail\":\"Rate limit exceeded\"}");
    }

    private record BucketKey(String kind, String value) {
    }

}
