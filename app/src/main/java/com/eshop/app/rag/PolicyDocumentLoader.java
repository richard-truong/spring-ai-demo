package com.eshop.app.rag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PolicyDocumentLoader {

    private static final String RESOURCE = "rag/policies.txt";

    public List<PolicyNode> load() {
        List<PolicyNode> nodes = new ArrayList<>();
        PolicyNode currentProduct = null;
        PolicyNode currentSection = null;
        int clauseIndex = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(openResource(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (trimmed.startsWith("## ")) {
                    String sectionTitle = trimmed.substring(3).trim();
                    requireProduct(currentProduct, sectionTitle);
                    currentSection = new PolicyNode(
                        sectionId(currentProduct.id(), sectionTitle),
                        currentProduct.id(),
                        2,
                        currentProduct.productName(),
                        section(sectionTitle),
                        currentProduct.productName() + " — " + sectionTitle,
                        "");
                    nodes.add(currentSection);
                    clauseIndex = 0;
                } else if (trimmed.startsWith("# ")) {
                    String productName = trimmed.substring(2).trim();
                    currentProduct = new PolicyNode(slug(productName), null, 1, productName, null, productName, "");
                    nodes.add(currentProduct);
                    currentSection = null;
                } else if (trimmed.startsWith("- ")) {
                    requireSection(currentSection, trimmed);
                    String clause = trimmed.substring(2).trim();
                    PolicyNode clauseNode = new PolicyNode(
                        currentSection.id() + "-" + (clauseIndex + 1),
                        currentSection.id(),
                        3,
                        currentSection.productName(),
                        currentSection.section(),
                        currentSection.title(),
                        clause);
                    nodes.add(clauseNode);
                    clauseIndex++;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load policy document " + RESOURCE, e);
        }

        if (nodes.isEmpty()) {
            throw new IllegalStateException("No policy nodes parsed from " + RESOURCE);
        }
        return nodes;
    }

    private InputStream openResource() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream(RESOURCE);
        if (stream == null) {
            throw new IllegalStateException("Policy resource not found: " + RESOURCE);
        }
        return stream;
    }

    private static void requireProduct(PolicyNode product, String sectionTitle) {
        if (product == null) {
            throw new IllegalStateException("Section defined before any product: " + sectionTitle);
        }
    }

    private static void requireSection(PolicyNode section, String clause) {
        if (section == null) {
            throw new IllegalStateException("Clause defined before any section: " + clause);
        }
    }

    private static String sectionId(String productId, String sectionTitle) {
        return productId + "-" + slug(sectionTitle);
    }

    private static String section(String sectionTitle) {
        String lower = sectionTitle.toLowerCase(Locale.ROOT);
        if (lower.contains("refund") || lower.contains("hoan tien") || lower.contains("hoàn tiền")) {
            return "REFUND";
        }
        if (lower.contains("warranty") || lower.contains("bao hanh") || lower.contains("bảo hành")) {
            return "WARRANTY";
        }
        return slug(sectionTitle).toUpperCase(Locale.ROOT);
    }

    private static String slug(String value) {
        return value.trim().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-|-$)", "");
    }

}
