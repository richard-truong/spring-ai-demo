package com.eshop.core.application.port.in;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;

public interface ProductSuggestionUseCase {

    ProductSuggestion suggest(ProductSuggestionCommand command);

}
