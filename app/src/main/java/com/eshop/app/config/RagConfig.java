package com.eshop.app.config;

import com.eshop.app.rag.PolicyDocumentLoader;
import com.eshop.app.rag.PolicyIndexer;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.request.EmbeddingInputType;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class RagConfig {

    @Bean
    public PolicyDocumentLoader policyDocumentLoader() {
        return new PolicyDocumentLoader();
    }

    @Bean
    @Profile("rag-inmemory | rag-qdrant")
    public ContentRetriever policyContentRetriever(EmbeddingStore<TextSegment> embeddingStore,
                                                   EmbeddingModel embeddingModel,
                                                   @Value("${app.rag.max-results:4}") int maxResults,
                                                   @Value("${app.rag.min-score:0.0}") double minScore) {
        return EmbeddingStoreContentRetriever.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .embeddingInputType(EmbeddingInputType.QUERY)
            .maxResults(maxResults)
            .minScore(minScore)
            .build();
    }

    @Bean
    @Profile("rag-inmemory | rag-qdrant")
    public ApplicationRunner policyIndexingRunner(PolicyIndexer indexer) {
        return args -> indexer.index();
    }

}
