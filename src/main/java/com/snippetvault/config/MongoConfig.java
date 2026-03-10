package com.snippetvault.config;

import com.snippetvault.snippet.SnippetDocument;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("title", 3F)
                .onField("content", 2F)
                .onField("description", 1F)
                .build();

        mongoTemplate.indexOps(SnippetDocument.class).ensureIndex(textIndex);

        mongoTemplate.indexOps(SnippetDocument.class)
                .ensureIndex(new Index().on("language", Sort.Direction.ASC));

        mongoTemplate.indexOps(SnippetDocument.class)
                .ensureIndex(new Index().on("tags", Sort.Direction.ASC));

        mongoTemplate.indexOps(SnippetDocument.class)
                .ensureIndex(new Index().on("author.userId", Sort.Direction.ASC));

        mongoTemplate.indexOps(SnippetDocument.class)
                .ensureIndex(new Index()
                        .on("isPublic", Sort.Direction.ASC)
                        .on("viewCount", Sort.Direction.DESC));

        log.info("MongoDB indexes initialized");
    }
}