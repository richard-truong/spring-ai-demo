package com.eshop.app.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.request.EmbeddingRequest;
import dev.langchain4j.model.embedding.response.EmbeddingResponse;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemoryPolicyIndexerTest {

    @Test
    void indexesEveryNodeAndServesThemThroughSearch() {
        EmbeddingModel embeddingModel = mock(EmbeddingModel.class);
        Embedding fixed = new Embedding(new float[]{1f, 2f, 3f});
        when(embeddingModel.embed(any(EmbeddingRequest.class)))
            .thenReturn(EmbeddingResponse.builder().embeddings(List.of(fixed)).build());

        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        PolicyDocumentLoader loader = new PolicyDocumentLoader();
        new InMemoryPolicyIndexer(store, embeddingModel, loader).index();

        EmbeddingSearchResult<TextSegment> result = store.search(
            EmbeddingSearchRequest.builder()
                .query("hoàn tiền Espresso")
                .queryEmbedding(fixed)
                .maxResults(100)
                .build());

        assertThat(result.matches()).hasSize(loader.load().size());
    }

}
