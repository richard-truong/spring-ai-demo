package com.eshop.app.adapter.in.web;

import com.eshop.app.adapter.in.web.dto.LoginRequest;
import com.eshop.app.adapter.in.web.dto.RegisterRequest;
import com.eshop.app.adapter.in.web.dto.TokenResponse;
import com.eshop.app.adapter.in.web.dto.UserResponse;
import com.eshop.core.application.dto.LoginCommand;
import com.eshop.core.application.dto.RegisterCommand;
import com.eshop.core.application.port.in.LoginUseCase;
import com.eshop.core.application.port.in.RegisterUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;

    public AuthController(RegisterUseCase registerUseCase, LoginUseCase loginUseCase) {
        this.registerUseCase = registerUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command = new RegisterCommand(request.email(), request.password(), request.name());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(UserResponse.from(registerUseCase.register(command)));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        return ResponseEntity.ok(TokenResponse.from(loginUseCase.login(command)));
    }

}
