package com.snippetvault.snippet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SnippetRepository extends MongoRepository<SnippetDocument, String> {

    Page<SnippetDocument> findByProgrammingLanguageAndIsPublicTrue(String language, Pageable pageable);

    Page<SnippetDocument> findByTagsContainingAndIsPublicTrue(String tag, Pageable pageable);

    Page<SnippetDocument> findByAuthorUserIdAndIsPublicTrue(String userId, Pageable pageable);

    Page<SnippetDocument> findByAuthorUserId(String userId, Pageable pageable);

    @Query("{ $text: { $search: ?0 }, isPublic: true }")
    Page<SnippetDocument> fullTextSearch(String query, Pageable pageable);

    Page<SnippetDocument> findByCollectionIdAndIsPublicTrue(String collectionId, Pageable pageable);

    Page<SnippetDocument> findByCollectionId(String collectionId, Pageable pageable);
}