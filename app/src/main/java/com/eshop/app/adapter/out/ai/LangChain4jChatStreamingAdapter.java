package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.port.in.StreamSink;
import com.eshop.core.application.port.out.ChatStreamingPort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("langchain4j")
public class LangChain4jChatStreamingAdapter implements ChatStreamingPort {

    private final ChatStreamingAssistant assistant;

    public LangChain4jChatStreamingAdapter(ChatStreamingAssistant assistant) {
        this.assistant = assistant;
    }

    @Override
    public void reply(ChatMemoryId memoryId, String userMessage, StreamSink sink) {
        assistant.chat(memoryId, userMessage)
            .onPartialResponse(sink::onToken)
            .onCompleteResponse(response -> sink.onComplete())
            .onError(sink::onError)
            .start();
    }

}
