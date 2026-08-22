package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.LoginCommand;
import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.application.port.in.LoginUseCase;
import com.eshop.core.application.port.out.PasswordEncoderPort;
import com.eshop.core.application.port.out.TokenProviderPort;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.exception.InvalidCredentialsException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;

public class LoginUseCaseImpl implements LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final TokenProviderPort tokenProvider;

    public LoginUseCaseImpl(UserRepositoryPort userRepository,
                            PasswordEncoderPort passwordEncoder,
                            TokenProviderPort tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public TokenResult login(LoginCommand command) {
        Email email = new Email(command.email());
        if (command.password() == null || command.password().isBlank()) {
            throw new InvalidCredentialsException();
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.issue(user.id(), user.email().value(), user.role());
    }

}
