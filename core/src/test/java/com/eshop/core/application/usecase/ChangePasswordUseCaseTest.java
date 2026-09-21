package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChangePasswordCommand;
import com.eshop.core.application.port.in.ChangePasswordUseCase;
import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.InvalidCredentialsException;
import com.eshop.core.domain.exception.InvalidCurrentPasswordException;
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

class ChangePasswordUseCaseTest {

    private static final Instant CREATED_AT = Instant.parse("2026-08-18T10:00:00Z");

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final FakePasswordEncoder passwordEncoder = new FakePasswordEncoder();
    private final ChangePasswordUseCase useCase =
        new ChangePasswordUseCaseImpl(userRepository, passwordEncoder);

    @BeforeEach
    void seedUser() {
        userRepository.save(new User(
            "user-1",
            new Email("alice@example.com"),
            "Alice",
            passwordEncoder.encode("S3cret!Pass"),
            Role.CUSTOMER,
            CREATED_AT
        ));
    }

    @Test
    void replacesPasswordHash() {
        useCase.changePassword(new ChangePasswordCommand("user-1", "S3cret!Pass", "N3w!Passw0rd"));

        User saved = userRepository.findById("user-1").orElseThrow();
        assertThat(saved.passwordHash()).isEqualTo("hashed:N3w!Passw0rd");
        assertThat(saved.passwordHash()).isNotEqualTo("N3w!Passw0rd");
    }

    @Test
    void preservesOtherFields() {
        useCase.changePassword(new ChangePasswordCommand("user-1", "S3cret!Pass", "N3w!Passw0rd"));

        User saved = userRepository.findById("user-1").orElseThrow();
        assertThat(saved.id()).isEqualTo("user-1");
        assertThat(saved.email()).isEqualTo(new Email("alice@example.com"));
        assertThat(saved.name()).isEqualTo("Alice");
        assertThat(saved.role()).isEqualTo(Role.CUSTOMER);
        assertThat(saved.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void acceptsNewPasswordAtBoundaries() {
        useCase.changePassword(new ChangePasswordCommand("user-1", "S3cret!Pass", "P".repeat(8)));
        assertThat(userRepository.findById("user-1").orElseThrow().passwordHash())
            .isEqualTo("hashed:" + "P".repeat(8));

        useCase.changePassword(
            new ChangePasswordCommand("user-1", "P".repeat(8), "P".repeat(72)));
        assertThat(userRepository.findById("user-1").orElseThrow().passwordHash())
            .isEqualTo("hashed:" + "P".repeat(72));
    }

    @Test
    void rejectsWrongCurrentPasswordWithoutWriting() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", "wrong-pass", "N3w!Passw0rd")))
            .isInstanceOf(InvalidCurrentPasswordException.class);

        assertThat(userRepository.findById("user-1").orElseThrow().passwordHash())
            .isEqualTo("hashed:S3cret!Pass");
    }

    @Test
    void rejectsBlankCurrentPassword() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", "   ", "N3w!Passw0rd")))
            .isInstanceOf(InvalidCurrentPasswordException.class);
    }

    @Test
    void rejectsNullCurrentPassword() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", null, "N3w!Passw0rd")))
            .isInstanceOf(InvalidCurrentPasswordException.class);
    }

    @Test
    void rejectsUnknownUser() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("nobody", "S3cret!Pass", "N3w!Passw0rd")))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsTooShortNewPassword() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", "S3cret!Pass", "Short1!")))
            .isInstanceOf(DomainException.class);

        assertThat(userRepository.findById("user-1").orElseThrow().passwordHash())
            .isEqualTo("hashed:S3cret!Pass");
    }

    @Test
    void rejectsTooLongNewPassword() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", "S3cret!Pass", "P".repeat(73))))
            .isInstanceOf(DomainException.class);

        assertThat(userRepository.findById("user-1").orElseThrow().passwordHash())
            .isEqualTo("hashed:S3cret!Pass");
    }

    @Test
    void rejectsBlankNewPassword() {
        assertThatThrownBy(() -> useCase.changePassword(
            new ChangePasswordCommand("user-1", "S3cret!Pass", "   ")))
            .isInstanceOf(DomainException.class);
    }

}
