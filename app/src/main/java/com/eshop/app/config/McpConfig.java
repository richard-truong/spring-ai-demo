package com.eshop.app.config;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.service.tool.ToolProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("langchain4j")
public class McpConfig {

    @Bean(destroyMethod = "close")
    public McpClient eshopMcpClient(@Value("${app.mcp.server-url:http://localhost:8080/mcp}") String serverUrl) {
        McpTransport transport = StreamableHttpMcpTransport.builder()
            .url(serverUrl)
            .build();
        return DefaultMcpClient.builder()
            .key("eshop-mcp-client")
            .transport(transport)
            .build();
    }

    @Bean
    public ToolProvider mcpToolProvider(McpClient eshopMcpClient) {
        return McpToolProvider.builder()
            .mcpClients(eshopMcpClient)
            .build();
    }

}
