package com.eshop.core.application.dto;

import com.eshop.core.domain.vo.ChatRole;

public record ChatMessage(
    ChatRole role,
    String content
) {
}
