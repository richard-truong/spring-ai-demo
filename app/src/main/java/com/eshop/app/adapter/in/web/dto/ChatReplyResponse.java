package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.ChatReply;

public record ChatReplyResponse(
    String reply
) {

    public static ChatReplyResponse from(ChatReply reply) {
        return new ChatReplyResponse(reply.content());
    }

}
