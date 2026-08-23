package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.ChatCommand;

public interface ChatStreamingUseCase {

    void reply(ChatCommand command, StreamSink sink);

}
