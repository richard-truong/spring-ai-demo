package com.eshop.app.config;

import com.eshop.app.rag.PolicyDocumentLoader;
import com.eshop.app.rag.PolicyIndexer;
import com.eshop.app.rag.QdrantPolicyIndexer;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rag-qdrant")
public class QdrantRagConfig {

    @Bean(destroyMethod = "close")
    public QdrantClient qdrantClient(@Value("${app.rag.qdrant.host:localhost}") String host,
                                     @Value("${app.rag.qdrant.port:6334}") int port,
                                     @Value("${app.rag.qdrant.api-key:}") String apiKey) {
        QdrantGrpcClient.Builder builder = QdrantGrpcClient.newBuilder(host, port, false);
        if (apiKey != null && !apiKey.isBlank()) {
            builder.withApiKey(apiKey);
        }
        return new QdrantClient(builder.build());
    }

    @Bean
    public QdrantEmbeddingStore qdrantEmbeddingStore(QdrantClient client,
                                                     @Value("${app.rag.collection:eshop-policies}") String collection) {
        return QdrantEmbeddingStore.builder()
            .client(client)
            .collectionName(collection)
            .build();
    }

    @Bean
    public PolicyIndexer qdrantPolicyIndexer(QdrantClient client,
                                             QdrantEmbeddingStore store,
                                             EmbeddingModel embeddingModel,
                                             PolicyDocumentLoader loader,
                                             @Value("${app.rag.collection:eshop-policies}") String collection) {
        return new QdrantPolicyIndexer(client, store, embeddingModel, loader, collection);
    }

}
