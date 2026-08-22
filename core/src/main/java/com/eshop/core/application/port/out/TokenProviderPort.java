package com.eshop.core.application.port.out;

import com.eshop.core.application.dto.TokenResult;
import com.eshop.core.domain.vo.Role;

public interface TokenProviderPort {

    TokenResult issue(String userId, String email, Role role);

}
