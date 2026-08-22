package com.eshop.app.config;

import com.eshop.core.application.port.out.ClockPort;
import com.eshop.core.application.port.out.IdGeneratorPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.UUID;

@Configuration
public class ApplicationConfig {

    @Bean
    public IdGeneratorPort idGenerator() {
        return () -> UUID.randomUUID().toString();
    }

    @Bean
    public ClockPort clock() {
        return Instant::now;
    }

}
