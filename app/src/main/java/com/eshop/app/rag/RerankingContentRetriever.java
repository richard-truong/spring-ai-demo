package com.eshop.app.rag;

import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.ContentMetadata;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RerankingContentRetriever implements ContentRetriever {

    private static final Logger log = LoggerFactory.getLogger(RerankingContentRetriever.class);

    private final ContentRetriever delegate;
    private final double keywordWeight;

    public RerankingContentRetriever(ContentRetriever delegate, double keywordWeight) {
        this.delegate = delegate;
        this.keywordWeight = keywordWeight;
    }

    @Override
    public List<Content> retrieve(Query query) {
        List<Content> results = rerank(query.text(), delegate.retrieve(query));
        logResults(query.text(), results);
        return results;
    }

    private List<Content> rerank(String queryText, List<Content> results) {
        Set<String> queryTerms = tokenize(queryText);
        return results.stream()
            .map(content -> scored(content, queryTerms))
            .sorted(Comparator.comparingDouble(Scored::rerankedScore).reversed())
            .map(Scored::content)
            .toList();
    }

    private Scored scored(Content content, Set<String> queryTerms) {
        double vectorScore = number(content.metadata().get(ContentMetadata.SCORE));
        double keywordScore = keywordScore(content.textSegment().text(), queryTerms);
        double rerankedScore = (1 - keywordWeight) * vectorScore + keywordWeight * keywordScore;

        Map<ContentMetadata, Object> metadata = new HashMap<>(content.metadata());
        metadata.put(ContentMetadata.RERANKED_SCORE, rerankedScore);
        return new Scored(Content.from(content.textSegment(), metadata), rerankedScore);
    }

    private static double number(Object value) {
        return value instanceof Number n ? n.doubleValue() : 0.0;
    }

    private static double keywordScore(String text, Set<String> queryTerms) {
        if (queryTerms.isEmpty()) {
            return 0.0;
        }
        Set<String> textTerms = tokenize(text);
        long matched = queryTerms.stream().filter(textTerms::contains).count();
        return (double) matched / queryTerms.size();
    }

    private static Set<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
            .filter(term -> !term.isBlank())
            .collect(Collectors.toSet());
    }

    private static void logResults(String queryText, List<Content> results) {
        log.info("RAG retrieved {} results for query \"{}\"", results.size(), queryText);
        for (Content content : results) {
            Map<String, Object> meta = content.textSegment().metadata().toMap();
            log.info("  nodeId={} product={} section={} level={} score={} reranked={} text={}",
                meta.get("nodeId"),
                meta.get("productName"),
                meta.get("section"),
                meta.get("level"),
                content.metadata().get(ContentMetadata.SCORE),
                content.metadata().get(ContentMetadata.RERANKED_SCORE),
                content.textSegment().text());
        }
    }

    private record Scored(Content content, double rerankedScore) {
    }

}
