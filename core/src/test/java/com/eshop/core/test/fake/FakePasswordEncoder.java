package com.eshop.core.test.fake;

import com.eshop.core.application.port.out.PasswordEncoderPort;

public class FakePasswordEncoder implements PasswordEncoderPort {

    @Override
    public String encode(String rawPassword) {
        return "hashed:" + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }

}
