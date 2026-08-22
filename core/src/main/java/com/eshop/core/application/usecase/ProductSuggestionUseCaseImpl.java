package com.eshop.core.application.usecase;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.in.ProductSuggestionUseCase;
import com.eshop.core.application.port.out.ProductSuggestionPort;

public class ProductSuggestionUseCaseImpl implements ProductSuggestionUseCase {

    private final ProductSuggestionPort productSuggestionPort;

    public ProductSuggestionUseCaseImpl(ProductSuggestionPort productSuggestionPort) {
        this.productSuggestionPort = productSuggestionPort;
    }

    @Override
    public ProductSuggestion suggest(ProductSuggestionCommand command) {
        return productSuggestionPort.suggest(command);
    }

}
