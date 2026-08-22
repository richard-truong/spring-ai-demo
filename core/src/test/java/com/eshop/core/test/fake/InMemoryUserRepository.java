package com.eshop.core.test.fake;

import com.eshop.core.application.port.out.UserRepositoryPort;
import com.eshop.core.domain.model.User;
import com.eshop.core.domain.vo.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryUserRepository implements UserRepositoryPort {

    private final Map<String, User> store = new HashMap<>();

    @Override
    public User save(User user) {
        store.put(user.id(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return store.values().stream()
            .filter(user -> user.email().equals(email))
            .findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }

}
