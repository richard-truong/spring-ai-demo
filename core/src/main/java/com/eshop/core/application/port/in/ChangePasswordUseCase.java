package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.ChangePasswordCommand;

public interface ChangePasswordUseCase {

    void changePassword(ChangePasswordCommand command);

}
