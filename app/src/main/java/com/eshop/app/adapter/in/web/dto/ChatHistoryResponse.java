package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.ChatMessage;

import java.util.List;

public record ChatHistoryResponse(
    String sessionId,
    List<ChatMessageResponse> messages
) {

    public static ChatHistoryResponse from(String sessionId, List<ChatMessage> messages) {
        return new ChatHistoryResponse(sessionId, messages.stream().map(ChatMessageResponse::from).toList());
    }

}
