package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.AuthenticatedUser;
import com.eshop.app.adapter.in.web.dto.ChatHistoryResponse;
import com.eshop.app.adapter.in.web.dto.ChatReplyResponse;
import com.eshop.app.adapter.in.web.dto.ChatRequest;
import com.eshop.core.application.dto.ChatCommand;
import com.eshop.core.application.port.in.ChatUseCase;
import jakarta.validation.Valid;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@Profile("langchain4j")
public class ChatController {

    private final ChatUseCase chatUseCase;

    public ChatController(ChatUseCase chatUseCase) {
        this.chatUseCase = chatUseCase;
    }

    @PostMapping
    public ResponseEntity<ChatReplyResponse> reply(@Valid @RequestBody ChatRequest request,
                                                   Authentication authentication) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        ChatCommand command = new ChatCommand(principal.id(), request.sessionId(), request.message());
        return ResponseEntity.ok(ChatReplyResponse.from(chatUseCase.reply(command)));
    }

    @GetMapping("/{sessionId}/history")
    public ResponseEntity<ChatHistoryResponse> history(@PathVariable String sessionId,
                                                       Authentication authentication) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        return ResponseEntity.ok(ChatHistoryResponse.from(sessionId, chatUseCase.history(principal.id(), sessionId)));
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> clear(@PathVariable String sessionId,
                                      Authentication authentication) {
        AuthenticatedUser principal = (AuthenticatedUser) authentication.getPrincipal();
        chatUseCase.clear(principal.id(), sessionId);
        return ResponseEntity.noContent().build();
    }

}
