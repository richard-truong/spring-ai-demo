package com.eshop.app.adapter.out.persistence;

import com.eshop.app.adapter.out.persistence.entity.UserEntity;
import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.exception.EmailAlreadyUsedException;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository repository;

    public UserRepositoryAdapter(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public User save(User user) {
        try {
            return toDomain(repository.save(toEntity(user)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyUsedException(user.email().value());
        }
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return repository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return repository.existsByEmail(email.value());
    }

    private UserEntity toEntity(User user) {
        return new UserEntity(
            user.id(),
            user.email().value(),
            user.name(),
            user.passwordHash(),
            user.role(),
            user.createdAt()
        );
    }

    private User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            new Email(entity.getEmail()),
            entity.getName(),
            entity.getPasswordHash(),
            entity.getRole(),
            entity.getCreatedAt()
        );
    }

}
