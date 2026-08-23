package com.eshop.app.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitPropertiesBindingTest {

    @Test
    void bindsAuthRuleLimitFromApplicationYml() throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> sources =
            loader.load("application.yml", new FileSystemResource("src/main/resources/application.yml"));

        Binder binder = new Binder(ConfigurationPropertySources.from(sources));

        RateLimitProperties properties =
            binder.bind("app.rate-limit", RateLimitProperties.class).get();

        assertThat(properties.getDefaultLimit())
            .isEqualTo(new RateLimitProperties.Limit(60, 30, 60));

        assertThat(properties.getRules()).hasSize(1);
        RateLimitProperties.Rule rule = properties.getRules().get(0);
        assertThat(rule.pathPattern()).isEqualTo("/api/v1/auth/**");
        assertThat(rule.limit()).isEqualTo(new RateLimitProperties.Limit(5, 1, 60));
    }
}
