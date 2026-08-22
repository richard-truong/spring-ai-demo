package com.eshop.core.test.fake;

import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.application.port.out.TokenProviderPort;
import com.eshop.core.domain.vo.Role;

public class FakeTokenProvider implements TokenProviderPort {

    @Override
    public TokenResult issue(String userId, String email, Role role) {
        return new TokenResult("token-" + userId, "Bearer", 3600);
    }

}
