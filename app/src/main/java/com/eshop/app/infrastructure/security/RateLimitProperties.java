package com.eshop.app.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
public class RateLimitProperties {

    private Limit defaultLimit = new Limit(60, 30, 60);
    private List<Rule> rules = List.of();

    public Limit getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(Limit defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public record Limit(int capacity, int refillTokens, int refillPeriodSeconds) {
    }

    public record Rule(String pathPattern, Limit limit) {
    }

}
