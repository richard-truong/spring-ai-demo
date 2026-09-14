CREATE TABLE chat_memory (
    id         BIGSERIAL PRIMARY KEY,
    user_id    VARCHAR(36)  NOT NULL,
    session_id VARCHAR(255) NOT NULL,
    messages   TEXT         NOT NULL,
    CONSTRAINT uq_chat_memory_user_session UNIQUE (user_id, session_id)
);
