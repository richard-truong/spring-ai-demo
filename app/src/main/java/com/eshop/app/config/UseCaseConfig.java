package com.eshop.app.config;

import com.eshop.core.application.port.in.LoginUseCase;
import com.eshop.core.application.port.in.ProductSuggestionUseCase;
import com.eshop.core.application.port.in.PurchaseUseCase;
import com.eshop.core.application.port.in.RegisterUseCase;
import com.eshop.core.application.port.out.ClockPort;
import com.eshop.core.application.port.out.IdGeneratorPort;
import com.eshop.core.application.port.out.OrderRepositoryPort;
import com.eshop.core.application.port.out.PasswordEncoderPort;
import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.application.port.out.ProductSuggestionPort;
import com.eshop.core.application.port.out.TokenProviderPort;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.application.usecase.LoginUseCaseImpl;
import com.eshop.core.application.usecase.ProductSuggestionUseCaseImpl;
import com.eshop.core.application.usecase.PurchaseUseCaseImpl;
import com.eshop.core.application.usecase.RegisterUseCaseImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterUseCase registerUseCase(UserRepositoryPort userRepository,
                                           PasswordEncoderPort passwordEncoder,
                                           IdGeneratorPort idGenerator,
                                           ClockPort clock) {
        return new RegisterUseCaseImpl(userRepository, passwordEncoder, idGenerator, clock);
    }

    @Bean
    public LoginUseCase loginUseCase(UserRepositoryPort userRepository,
                                     PasswordEncoderPort passwordEncoder,
                                     TokenProviderPort tokenProvider) {
        return new LoginUseCaseImpl(userRepository, passwordEncoder, tokenProvider);
    }

    @Bean
    public PurchaseUseCase purchaseUseCase(OrderRepositoryPort orderRepository,
                                           ProductRepositoryPort productRepository,
                                           IdGeneratorPort idGenerator,
                                           ClockPort clock) {
        return new PurchaseUseCaseImpl(orderRepository, productRepository, idGenerator, clock);
    }

    @Bean
    public ProductSuggestionUseCase productSuggestionUseCase(ProductSuggestionPort productSuggestionPort) {
        return new ProductSuggestionUseCaseImpl(productSuggestionPort);
    }

}
