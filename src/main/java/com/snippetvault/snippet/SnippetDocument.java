package com.snippetvault.snippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "snippets")
public class SnippetDocument {

    @Id
    private String id;

    @TextIndexed(weight = 3)   
    private String title;

    @Indexed
    private String language;

    @TextIndexed(weight = 1)
    private String description;

    @TextIndexed(weight = 2)
    private String content;

    @Indexed
    private List<String> tags = new ArrayList<>();

    private SnippetAuthor author;

    private String collectionId;

    private boolean isPublic;

    @Builder.Default
    private int forkCount = 0;

    private String forkedFrom;

    @Builder.Default
    private int viewCount = 0;

    @Builder.Default
    private List<SnippetVersion> versionHistory = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}