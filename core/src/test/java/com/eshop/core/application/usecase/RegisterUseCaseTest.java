package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.RegisterCommand;
import com.eshop.core.application.dto.UserResult;
import com.eshop.core.application.port.in.RegisterUseCase;
import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.EmailAlreadyUsedException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;
import com.eshop.core.domain.vo.Role;
import com.eshop.core.test.fake.FakePasswordEncoder;
import com.eshop.core.test.fake.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterUseCaseTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final FakePasswordEncoder passwordEncoder = new FakePasswordEncoder();
    private final RegisterUseCase useCase = new RegisterUseCaseImpl(
        userRepository,
        passwordEncoder,
        () -> "user-1",
        () -> Instant.parse("2026-08-18T10:00:00Z")
    );

    @BeforeEach
    void setUp() {
    }

    @Test
    void registersNewUserWithHashedPassword() {
        UserResult result = useCase.register(
            new RegisterCommand("alice@example.com", "S3cret!Pass", "Alice"));

        assertThat(result.id()).isEqualTo("user-1");
        assertThat(result.email()).isEqualTo("alice@example.com");
        assertThat(result.name()).isEqualTo("Alice");

        User saved = userRepository.findByEmail(new Email("alice@example.com")).orElseThrow();
        assertThat(saved.passwordHash()).isEqualTo("hashed:S3cret!Pass");
        assertThat(saved.passwordHash()).isNotEqualTo("S3cret!Pass");
        assertThat(saved.role()).isEqualTo(Role.CUSTOMER);
        assertThat(saved.createdAt()).isEqualTo(Instant.parse("2026-08-18T10:00:00Z"));
    }

    @Test
    void rejectsDuplicateEmail() {
        useCase.register(new RegisterCommand("alice@example.com", "S3cret!Pass", "Alice"));

        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "Another!Pass", "Bob")))
            .isInstanceOf(EmailAlreadyUsedException.class);
    }

    @Test
    void rejectsBlankName() {
        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "S3cret!Pass", "   ")))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsBlankPassword() {
        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "   ", "Alice")))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsTooShortPassword() {
        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "Short1!", "Alice")))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsTooLongPassword() {
        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "P".repeat(73), "Alice")))
            .isInstanceOf(DomainException.class);
    }

    @Test
    void rejectsTooLongName() {
        assertThatThrownBy(() -> useCase.register(
            new RegisterCommand("alice@example.com", "S3cret!Pass", "N".repeat(101))))
            .isInstanceOf(DomainException.class);
    }

}
