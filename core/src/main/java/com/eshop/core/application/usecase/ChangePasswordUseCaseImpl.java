package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChangePasswordCommand;
import com.eshop.core.application.port.in.ChangePasswordUseCase;
import com.eshop.core.application.port.out.PasswordEncoderPort;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.exception.InvalidCredentialsException;
import com.eshop.core.domain.exception.InvalidCurrentPasswordException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Password;

public class ChangePasswordUseCaseImpl implements ChangePasswordUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;

    public ChangePasswordUseCaseImpl(UserRepositoryPort userRepository,
                                     PasswordEncoderPort passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void changePassword(ChangePasswordCommand command) {
        User user = userRepository.findById(command.userId())
            .orElseThrow(InvalidCredentialsException::new);

        if (command.currentPassword() == null || command.currentPassword().isBlank()
                || !passwordEncoder.matches(command.currentPassword(), user.passwordHash())) {
            throw new InvalidCurrentPasswordException();
        }

        Password newPassword = new Password(command.newPassword());

        userRepository.save(user.changePassword(passwordEncoder.encode(newPassword.value())));
    }

}
