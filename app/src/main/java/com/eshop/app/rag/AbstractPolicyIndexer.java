package com.eshop.app.rag;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.request.EmbeddingInputType;
import dev.langchain4j.model.embedding.request.EmbeddingRequest;
import dev.langchain4j.model.embedding.response.EmbeddingResponse;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractPolicyIndexer implements PolicyIndexer {

    protected final EmbeddingStore<TextSegment> store;
    protected final EmbeddingModel embeddingModel;
    protected final PolicyDocumentLoader loader;

    protected AbstractPolicyIndexer(EmbeddingStore<TextSegment> store,
                                    EmbeddingModel embeddingModel,
                                    PolicyDocumentLoader loader) {
        this.store = store;
        this.embeddingModel = embeddingModel;
        this.loader = loader;
    }

    @Override
    public final void index() {
        prepareStore();
        List<PolicyNode> nodes = loader.load();
        List<TextSegment> segments = nodes.stream().map(AbstractPolicyIndexer::toSegment).toList();
        List<Embedding> embeddings = embedDocuments(segments);
        List<String> ids = segments.stream().map(s -> UUID.randomUUID().toString()).toList();
        store.addAll(ids, embeddings, segments);
    }

    protected void prepareStore() {
    }

    protected Embedding embedDocument(String text) {
        EmbeddingResponse response = embeddingModel.embed(
            EmbeddingRequest.builder().input(text).inputType(EmbeddingInputType.DOCUMENT).build());
        return response.embeddings().get(0);
    }

    private static TextSegment toSegment(PolicyNode node) {
        Map<String, Object> values = new HashMap<>();
        values.put("nodeId", node.id());
        values.put("level", node.level());
        values.put("productName", node.productName());
        if (node.parentId() != null) {
            values.put("parentId", node.parentId());
        }
        if (node.section() != null) {
            values.put("section", node.section());
        }
        return TextSegment.from(node.searchableText(), Metadata.from(values));
    }

    private List<Embedding> embedDocuments(List<TextSegment> segments) {
        List<Embedding> embeddings = new ArrayList<>(segments.size());
        for (TextSegment segment : segments) {
            embeddings.add(embedDocument(segment.text()));
        }
        return embeddings;
    }

}
