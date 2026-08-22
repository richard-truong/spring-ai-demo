package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.LoginCommand;
import com.eshop.core.application.dto.TokenResult;

public interface LoginUseCase {

    TokenResult login(LoginCommand command);

}
