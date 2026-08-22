package com.eshop.app.adapter.in.web.dto;

import com.eshop.core.application.dto.UserResult;

public record UserResponse(
    String id,
    String email,
    String name
) {

    public static UserResponse from(UserResult result) {
        return new UserResponse(result.id(), result.email(), result.name());
    }

}
