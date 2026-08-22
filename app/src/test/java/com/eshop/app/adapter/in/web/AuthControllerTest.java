package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.security.JwtAuthFilter;
import com.eshop.app.adapter.in.security.SecurityConfig;
import com.eshop.app.adapter.out.security.JwtTokenProvider;
import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.application.dto.UserResult;
import com.eshop.core.application.port.in.LoginUseCase;
import com.eshop.core.application.port.in.RegisterUseCase;
import com.eshop.core.domain.exception.EmailAlreadyUsedException;
import com.eshop.core.domain.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtTokenProvider.class})
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RegisterUseCase registerUseCase;

    @MockitoBean
    LoginUseCase loginUseCase;

    @Test
    void registerReturns201AndOmitsPasswordHash() throws Exception {
        when(registerUseCase.register(any()))
            .thenReturn(new UserResult("user-1", "alice@example.com", "Alice"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"S3cret!Pass\",\"name\":\"Alice\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value("user-1"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void registerValidationErrorReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"not-an-email\",\"password\":\"short\",\"name\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void registerDuplicateEmailReturns409() throws Exception {
        when(registerUseCase.register(any()))
            .thenThrow(new EmailAlreadyUsedException("alice@example.com"));

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"S3cret!Pass\",\"name\":\"Alice\"}"))
            .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsToken() throws Exception {
        when(loginUseCase.login(any()))
            .thenReturn(new TokenResult("jwt-token", "Bearer", 3600));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"S3cret!Pass\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("jwt-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(3600));
    }

    @Test
    void loginInvalidCredentialsReturns401() throws Exception {
        when(loginUseCase.login(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"wrong-pass\"}"))
            .andExpect(status().isUnauthorized());
    }

}
