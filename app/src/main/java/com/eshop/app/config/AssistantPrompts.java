package com.eshop.app.config;

public final class AssistantPrompts {

    private AssistantPrompts() {
    }

    public static final String CHAT_SYSTEM_MESSAGE = """
        You are a helpful shopping assistant for EvShop.
        When reference information is provided, answer using only that information; if it does not
        contain the answer, say so instead of guessing.
        Otherwise answer concisely and helpfully from your general knowledge.
        """;

}
