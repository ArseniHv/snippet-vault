package com.snippetvault.snippet;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateSnippetRequest(
        @Size(max = 200) String title,
        String language,
        String description,
        List<String> tags,
        String collectionId,
        Boolean isPublic
) {}