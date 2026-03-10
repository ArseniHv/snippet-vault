package com.snippetvault.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CollectionRepository extends MongoRepository<CollectionDocument, String> {
    Page<CollectionDocument> findByOwnerId(String ownerId, Pageable pageable);
    boolean existsByIdAndOwnerId(String id, String ownerId);
}