package com.eshop.core.application.port.out;

import com.eshop.core.application.dto.ChatMemoryId;
import com.eshop.core.application.port.in.StreamSink;

public interface ChatStreamingPort {

    void reply(ChatMemoryId memoryId, String userMessage, StreamSink sink);

}
