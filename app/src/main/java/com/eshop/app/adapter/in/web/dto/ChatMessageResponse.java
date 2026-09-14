package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.ChatMessage;

public record ChatMessageResponse(
    String role,
    String content
) {

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(message.role().name(), message.content());
    }

}
