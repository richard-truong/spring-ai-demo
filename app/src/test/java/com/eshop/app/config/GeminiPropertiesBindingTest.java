package com.eshop.app.config;

import dev.langchain4j.googleaigemini.spring.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeminiPropertiesBindingTest {

    @Test
    void bindsGeminiChatAndStreamingModelsFromProfileYml() throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> sources =
            loader.load("application-gemini.yml",
                new FileSystemResource("src/main/resources/application-gemini.yml"));

        Properties properties = new Binder(ConfigurationPropertySources.from(sources))
            .bind("langchain4j.google-ai-gemini", Properties.class)
            .get();

        assertThat(properties.getChatModel().modelName()).isEqualTo("gemini-3.6-flash");
        assertThat(properties.getStreamingChatModel()).isNotNull();
        assertThat(properties.getStreamingChatModel().modelName()).isEqualTo("gemini-3.6-flash");
        assertThat(properties.getStreamingChatModel().returnThinking()).isTrue();
        assertThat(properties.getStreamingChatModel().sendThinking()).isTrue();
        assertThat(properties.getEmbeddingModel().modelName()).isEqualTo("gemini-embedding-2-preview");
    }
}
