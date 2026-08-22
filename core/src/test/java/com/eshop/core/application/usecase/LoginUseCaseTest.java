package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.LoginCommand;
import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.application.port.in.LoginUseCase;
import com.eshop.core.domain.exception.InvalidCredentialsException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;
import com.eshop.core.domain.vo.Role;
import com.eshop.core.test.fake.FakePasswordEncoder;
import com.eshop.core.test.fake.FakeTokenProvider;
import com.eshop.core.test.fake.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginUseCaseTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final FakePasswordEncoder passwordEncoder = new FakePasswordEncoder();
    private final FakeTokenProvider tokenProvider = new FakeTokenProvider();
    private final LoginUseCase useCase =
        new LoginUseCaseImpl(userRepository, passwordEncoder, tokenProvider);

    @BeforeEach
    void seedUser() {
        userRepository.save(new User(
            "user-1",
            new Email("alice@example.com"),
            "Alice",
            passwordEncoder.encode("S3cret!Pass"),
            Role.CUSTOMER,
            Instant.now()
        ));
    }

    @Test
    void issuesTokenOnValidCredentials() {
        TokenResult result = useCase.login(new LoginCommand("alice@example.com", "S3cret!Pass"));

        assertThat(result.accessToken()).isEqualTo("token-user-1");
        assertThat(result.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void rejectsWrongPassword() {
        assertThatThrownBy(() -> useCase.login(new LoginCommand("alice@example.com", "wrong-pass")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsUnknownEmail() {
        assertThatThrownBy(() -> useCase.login(new LoginCommand("nobody@example.com", "S3cret!Pass")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsBlankPassword() {
        assertThatThrownBy(() -> useCase.login(new LoginCommand("alice@example.com", "   ")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

}
