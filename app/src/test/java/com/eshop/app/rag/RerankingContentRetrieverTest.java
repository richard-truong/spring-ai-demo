package com.eshop.app.rag;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.query.Query;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RerankingContentRetrieverTest {

    @Test
    void reordersByKeywordBoostAndStoresRerankedScore() {
        Content highKeyword = content("hoàn tiền Espresso trong 7 ngày", 0.4, "espresso-refund-1");
        Content lowKeyword = content("hạt cà phê không bảo hành", 0.9, "espresso-warranty-1");

        RerankingContentRetriever retriever = new RerankingContentRetriever(
            query -> List.of(highKeyword, lowKeyword), 0.6);

        List<Content> results = retriever.retrieve(Query.from("hoàn tiền Espresso"));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).textSegment().text()).contains("hoàn tiền");
        assertThat(results.get(0).metadata()).containsKey(ContentMetadata.RERANKED_SCORE);
    }

    private static Content content(String text, double score, String nodeId) {
        TextSegment segment = TextSegment.from(text, Metadata.from(Map.of("nodeId", nodeId)));
        Map<ContentMetadata, Object> metadata = new HashMap<>();
        metadata.put(ContentMetadata.SCORE, score);
        return Content.from(segment, metadata);
    }

}
