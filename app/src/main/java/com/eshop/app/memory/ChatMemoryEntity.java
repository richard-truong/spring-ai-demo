package com.eshop.app.memory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "chat_memory",
       uniqueConstraints = @UniqueConstraint(name = "uq_chat_memory_user_session",
                                             columnNames = {"user_id", "session_id"}))
public class ChatMemoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String messages;

    protected ChatMemoryEntity() {
    }

    public ChatMemoryEntity(String userId, String sessionId, String messages) {
        this(null, userId, sessionId, messages);
    }

    private ChatMemoryEntity(Long id, String userId, String sessionId, String messages) {
        this.id = id;
        this.userId = userId;
        this.sessionId = sessionId;
        this.messages = messages;
    }

    public ChatMemoryEntity withMessages(String newMessages) {
        return new ChatMemoryEntity(this.id, this.userId, this.sessionId, newMessages);
    }

    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getMessages() {
        return messages;
    }

}
