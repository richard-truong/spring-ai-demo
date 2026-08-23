package com.eshop.app.config;

import com.eshop.app.rag.InMemoryPolicyIndexer;
import com.eshop.app.rag.PolicyDocumentLoader;
import com.eshop.app.rag.PolicyIndexer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rag-inmemory")
public class InMemoryRagConfig {

    @Bean
    public InMemoryEmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    public PolicyIndexer inMemoryPolicyIndexer(EmbeddingStore<TextSegment> store,
                                               EmbeddingModel embeddingModel,
                                               PolicyDocumentLoader loader) {
        return new InMemoryPolicyIndexer(store, embeddingModel, loader);
    }

}
