package com.snippetvault.collection;

import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionResponse toResponse(CollectionDocument doc) {
        return new CollectionResponse(
                doc.getId(),
                doc.getName(),
                doc.getDescription(),
                doc.getOwnerId(),
                doc.getCreatedAt(),
                doc.getUpdatedAt()
        );
    }
}