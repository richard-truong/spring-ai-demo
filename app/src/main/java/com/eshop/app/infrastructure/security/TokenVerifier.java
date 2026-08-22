package com.eshop.app.infrastructure.security;

public interface TokenVerifier {

    VerifiedToken verify(String token);

}
