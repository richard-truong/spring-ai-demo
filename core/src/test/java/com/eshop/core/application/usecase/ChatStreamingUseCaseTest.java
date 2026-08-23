package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.port.in.StreamSink;
import com.eshop.core.application.port.out.ChatStreamingPort;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChatStreamingUseCaseTest {

    @Test
    void replyDelegatesWithMemoryIdBuiltFromUserAndSession() {
        RecordingChatStreamingPort port = new RecordingChatStreamingPort();
        ChatStreamingUseCaseImpl useCase = new ChatStreamingUseCaseImpl(port);

        useCase.reply(new ChatCommand("user-1", "session-42", "hello"), new RecordingStreamSink());

        assertThat(port.replyMemoryId).isEqualTo(new ChatMemoryId("user-1", "session-42"));
        assertThat(port.replyMessage).isEqualTo("hello");
    }

    @Test
    void replyForwardsTheSameSinkToThePort() {
        RecordingChatStreamingPort port = new RecordingChatStreamingPort();
        ChatStreamingUseCaseImpl useCase = new ChatStreamingUseCaseImpl(port);
        RecordingStreamSink sink = new RecordingStreamSink();

        useCase.reply(new ChatCommand("user-1", "session-1", "hello"), sink);

        assertThat(port.replySink).isSameAs(sink);
    }

    private static final class RecordingChatStreamingPort implements ChatStreamingPort {

        ChatMemoryId replyMemoryId;
        String replyMessage;
        StreamSink replySink;

        @Override
        public void reply(ChatMemoryId memoryId, String userMessage, StreamSink sink) {
            this.replyMemoryId = memoryId;
            this.replyMessage = userMessage;
            this.replySink = sink;
        }

    }

    private static final class RecordingStreamSink implements StreamSink {

        final List<String> tokens = new ArrayList<>();
        boolean completed;
        Throwable error;

        @Override
        public void onToken(String token) {
            tokens.add(token);
        }

        @Override
        public void onComplete() {
            this.completed = true;
        }

        @Override
        public void onError(Throwable error) {
            this.error = error;
        }

    }

}
