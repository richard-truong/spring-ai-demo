package com.eshop.app.rag;

public record PolicyNode(
    String id,
    String parentId,
    int level,
    String productName,
    String section,
    String title,
    String content
) {

    public String searchableText() {
        if (content == null || content.isBlank()) {
            return title;
        }
        return title + ": " + content;
    }

}
