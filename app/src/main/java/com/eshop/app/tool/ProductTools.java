package com.eshop.app.tool;

import com.eshop.core.application.dto.ProductSuggestion;
import com.eshop.core.application.dto.ProductSuggestionCommand;
import com.eshop.core.application.port.out.ProductSuggestionPort;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("langchain4j")
public class ProductTools {

    private final ProductSuggestionPort productSuggestionPort;

    public ProductTools(ProductSuggestionPort productSuggestionPort) {
        this.productSuggestionPort = productSuggestionPort;
    }

    @Tool("Search for a similar product on a target e-commerce platform (e.g. shopee, lazada) and return its name, price and description.")
    public ProductSuggestion searchProductOnPlatform(
            @P("The product name to search for") String productName,
            @P("The target e-commerce platform (e.g. shopee, lazada)") String platform) {
        return productSuggestionPort.suggest(new ProductSuggestionCommand(productName, platform));
    }

}
