package com.snippetvault.snippet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateSnippetRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank String content,
        @NotBlank String language,
        String description,
        List<String> tags,
        String collectionId,
        boolean isPublic
) {}