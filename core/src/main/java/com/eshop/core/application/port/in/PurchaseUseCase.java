package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.OrderResult;
import com.eshop.core.application.dto.PurchaseCommand;

public interface PurchaseUseCase {

    OrderResult purchase(PurchaseCommand command);

}
