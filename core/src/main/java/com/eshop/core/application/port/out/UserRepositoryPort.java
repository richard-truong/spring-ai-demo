package com.eshop.core.application.port.out;

import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(Email email);

    boolean existsByEmail(Email email);

}
