package com.snippetvault.snippet;

import java.time.Instant;
import java.util.List;

public record SnippetResponse(
        String id,
        String title,
        String programmingLanguage,
        String description,
        String content,
        List<String> tags,
        String authorId,
        String authorUsername,
        String collectionId,
        boolean isPublic,
        int forkCount,
        String forkedFrom,
        int viewCount,
        List<SnippetVersion> versionHistory,
        Instant createdAt,
        Instant updatedAt
) {}