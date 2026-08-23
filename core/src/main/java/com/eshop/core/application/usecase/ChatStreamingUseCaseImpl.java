package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.port.in.ChatStreamingUseCase;
import com.eshop.core.application.port.in.StreamSink;
import com.eshop.core.application.port.out.ChatStreamingPort;

public class ChatStreamingUseCaseImpl implements ChatStreamingUseCase {

    private final ChatStreamingPort chatStreamingPort;

    public ChatStreamingUseCaseImpl(ChatStreamingPort chatStreamingPort) {
        this.chatStreamingPort = chatStreamingPort;
    }

    @Override
    public void reply(ChatCommand command, StreamSink sink) {
        chatStreamingPort.reply(
            new ChatMemoryId(command.userId(), command.sessionId()),
            command.message(),
            sink
        );
    }

}
