package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.RegisterCommand;
import com.eshop.core.application.dto.UserResult;
import com.eshop.core.application.port.in.RegisterUseCase;
import com.eshop.core.application.port.out.ClockPort;
import com.eshop.core.application.port.out.IdGeneratorPort;
import com.eshop.core.application.port.out.PasswordEncoderPort;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.exception.DomainException;
import com.eshop.core.domain.exception.EmailAlreadyUsedException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;
import com.eshop.core.domain.vo.Role;

public class RegisterUseCaseImpl implements RegisterUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final IdGeneratorPort idGenerator;
    private final ClockPort clock;

    public RegisterUseCaseImpl(UserRepositoryPort userRepository,
                               PasswordEncoderPort passwordEncoder,
                               IdGeneratorPort idGenerator,
                               ClockPort clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    @Override
    public UserResult register(RegisterCommand command) {
        Email email = new Email(command.email());
        if (command.name() == null || command.name().isBlank()) {
            throw new DomainException("name must not be blank");
        }
        String name = command.name().trim();
        if (name.length() > 100) {
            throw new DomainException("name must be at most 100 characters");
        }
        if (command.password() == null || command.password().isBlank()) {
            throw new DomainException("password must not be blank");
        }
        if (command.password().length() < 8 || command.password().length() > 72) {
            throw new DomainException("password must be between 8 and 72 characters");
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email.value());
        }

        String passwordHash = passwordEncoder.encode(command.password());
        User user = new User(
            idGenerator.nextId(),
            email,
            name,
            passwordHash,
            Role.CUSTOMER,
            clock.now()
        );

        User saved = userRepository.save(user);
        return new UserResult(saved.id(), saved.email().value(), saved.name());
    }

}
