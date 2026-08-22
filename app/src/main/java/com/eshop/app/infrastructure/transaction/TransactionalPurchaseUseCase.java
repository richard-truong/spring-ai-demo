package com.eshop.app.infrastructure.transaction;

import com.eshop.core.application.dto.OrderResult;
import com.eshop.core.application.dto.PurchaseCommand;
import com.eshop.core.application.port.in.PurchaseUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
public class TransactionalPurchaseUseCase implements PurchaseUseCase {

    private final PurchaseUseCase delegate;

    public TransactionalPurchaseUseCase(@Qualifier("purchaseUseCase") PurchaseUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public OrderResult purchase(PurchaseCommand command) {
        return delegate.purchase(command);
    }

}
