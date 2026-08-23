package com.eshop.app.adapter.out.ai;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.port.in.StreamSink;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LangChain4jChatStreamingAdapterTest {

    private static final ChatMemoryId MEMORY_ID = new ChatMemoryId("user-1", "session-1");

    private final ChatStreamingAssistant assistant = mock(ChatStreamingAssistant.class);
    private final TokenStream tokenStream = mock(TokenStream.class);
    private final LangChain4jChatStreamingAdapter adapter = new LangChain4jChatStreamingAdapter(assistant);

    @Test
    void streamsPartialResponsesToSink() {
        stubAssistant();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<String>> tokenCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(tokenStream.onPartialResponse(tokenCaptor.capture())).thenReturn(tokenStream);

        RecordingStreamSink sink = new RecordingStreamSink();
        adapter.reply(MEMORY_ID, "hello", sink);

        tokenCaptor.getValue().accept("Hel");
        tokenCaptor.getValue().accept("lo");

        verify(tokenStream).start();
        assertThat(sink.tokens).containsExactly("Hel", "lo");
    }

    @Test
    void forwardsCompletionToSink() {
        stubAssistant();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<ChatResponse>> completionCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(tokenStream.onCompleteResponse(completionCaptor.capture())).thenReturn(tokenStream);

        RecordingStreamSink sink = new RecordingStreamSink();
        adapter.reply(MEMORY_ID, "hello", sink);

        completionCaptor.getValue().accept(null);

        assertThat(sink.completed).isTrue();
    }

    @Test
    void forwardsErrorToSink() {
        stubAssistant();
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<Throwable>> errorCaptor = ArgumentCaptor.forClass(Consumer.class);
        when(tokenStream.onError(errorCaptor.capture())).thenReturn(tokenStream);

        RecordingStreamSink sink = new RecordingStreamSink();
        adapter.reply(MEMORY_ID, "hello", sink);
        RuntimeException failure = new RuntimeException("boom");

        errorCaptor.getValue().accept(failure);

        assertThat(sink.error).isSameAs(failure);
    }

    private void stubAssistant() {
        when(assistant.chat(MEMORY_ID, "hello")).thenReturn(tokenStream);
        when(tokenStream.onPartialResponse(any())).thenReturn(tokenStream);
        when(tokenStream.onCompleteResponse(any())).thenReturn(tokenStream);
        when(tokenStream.onError(any())).thenReturn(tokenStream);
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
