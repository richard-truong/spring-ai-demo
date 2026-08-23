package com.eshop.app.rag;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections.Distance;
import io.qdrant.client.grpc.Collections.VectorParams;

import java.util.concurrent.ExecutionException;

public class QdrantPolicyIndexer extends AbstractPolicyIndexer {

    private static final String PROBE_TEXT = "policy probe";

    private final QdrantClient client;
    private final String collectionName;

    public QdrantPolicyIndexer(QdrantClient client,
                               QdrantEmbeddingStore store,
                               EmbeddingModel embeddingModel,
                               PolicyDocumentLoader loader,
                               String collectionName) {
        super(store, embeddingModel, loader);
        this.client = client;
        this.collectionName = collectionName;
    }

    @Override
    protected void prepareStore() {
        ensureCollection();
        store.removeAll();
    }

    private void ensureCollection() {
        try {
            boolean exists = client.collectionExistsAsync(collectionName).get();
            if (!exists) {
                int dimension = embedDocument(PROBE_TEXT).dimension();
                client.createCollectionAsync(collectionName,
                    VectorParams.newBuilder().setSize(dimension).setDistance(Distance.Cosine).build()).get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while ensuring Qdrant collection", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Failed to ensure Qdrant collection", e);
        }
    }

}
