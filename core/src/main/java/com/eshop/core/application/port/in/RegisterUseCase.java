package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.RegisterCommand;
import com.eshop.core.application.dto.UserResult;

public interface RegisterUseCase {

    UserResult register(RegisterCommand command);

}
