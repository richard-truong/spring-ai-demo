package com.eshop.core.application.port.out;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;

public interface ProductSuggestionPort {

    ProductSuggestion suggest(ProductSuggestionCommand command);

}
