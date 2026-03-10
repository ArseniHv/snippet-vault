package com.snippetvault.snippet;

import org.springframework.stereotype.Component;

@Component
public class SnippetMapper {

    public SnippetResponse toResponse(SnippetDocument doc) {
        return new SnippetResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getProgrammingLanguage(),
                doc.getDescription(),
                doc.getContent(),
                doc.getTags(),
                doc.getAuthor().getUserId(),
                doc.getAuthor().getUsername(),
                doc.getCollectionId(),
                doc.isPublic(),
                doc.getForkCount(),
                doc.getForkedFrom(),
                doc.getViewCount(),
                doc.getVersionHistory(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}