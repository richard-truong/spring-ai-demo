package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.JwtAuthFilter;
import com.eshop.app.adapter.in.security.SecurityConfig;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.core.application.dto.ChatMessage;
import com.eshop.core.application.dto.ChatReply;
import com.eshop.core.application.port.in.ChatUseCase;
import com.eshop.core.domain.vo.ChatRole;
import com.eshop.core.domain.vo.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ActiveProfiles("langchain4j")
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class})
class ChatControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    JwtTokenProvider tokenProvider;

    @MockitoBean
    ChatUseCase chatUseCase;

    @Test
    void replyWithoutTokenReturns401() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":\"s1\",\"message\":\"hi\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void replyReturnsReply() throws Exception {
        when(chatUseCase.reply(any())).thenReturn(new ChatReply("Hello!"));

        mockMvc.perform(post("/api/v1/chat")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":\"s1\",\"message\":\"hi\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reply").value("Hello!"));
    }

    @Test
    void replyValidationErrorReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/chat")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sessionId\":\"\",\"message\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void historyReturnsMessages() throws Exception {
        when(chatUseCase.history("user-1", "s1")).thenReturn(List.of(
            new ChatMessage(ChatRole.USER, "hi"),
            new ChatMessage(ChatRole.AI, "hello")
        ));

        mockMvc.perform(get("/api/v1/chat/s1/history")
                .header(HttpHeaders.AUTHORIZATION, bearerToken()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sessionId").value("s1"))
            .andExpect(jsonPath("$.messages[0].role").value("USER"))
            .andExpect(jsonPath("$.messages[0].content").value("hi"))
            .andExpect(jsonPath("$.messages[1].role").value("AI"))
            .andExpect(jsonPath("$.messages[1].content").value("hello"));
    }

    @Test
    void clearReturns204() throws Exception {
        mockMvc.perform(delete("/api/v1/chat/s1")
                .header(HttpHeaders.AUTHORIZATION, bearerToken()))
            .andExpect(status().isNoContent());
    }

    private String bearerToken() {
        return "Bearer " + tokenProvider.issue("user-1", "alice@example.com", Role.CUSTOMER).accessToken();
    }

}
