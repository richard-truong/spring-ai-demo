package com.eshop.app.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatMemoryJpaRepository extends JpaRepository<ChatMemoryEntity, Long> {

    Optional<ChatMemoryEntity> findByUserIdAndSessionId(String userId, String sessionId);

}
