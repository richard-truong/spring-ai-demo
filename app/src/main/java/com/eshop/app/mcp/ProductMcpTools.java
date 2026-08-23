package com.eshop.app.mcp;

import com.eshop.core.application.port.out.ProductRepositoryPort;
import com.eshop.core.domain.model.Product;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ProductMcpTools {

    private final ProductRepositoryPort productRepository;

    public ProductMcpTools(ProductRepositoryPort productRepository) {
        this.productRepository = productRepository;
    }

    @McpTool(name = "getProduct", description = "Get a product by its id from the EvShop database")
    public Product getProduct(@McpToolParam(description = "The product id") String productId) {
        return productRepository.findById(productId).orElse(null);
    }

}
