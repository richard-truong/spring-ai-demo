package com.eshop.app.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class InMemoryPolicyIndexer extends AbstractPolicyIndexer {

    public InMemoryPolicyIndexer(EmbeddingStore<TextSegment> store,
                                 EmbeddingModel embeddingModel,
                                 PolicyDocumentLoader loader) {
        super(store, embeddingModel, loader);
    }

}
