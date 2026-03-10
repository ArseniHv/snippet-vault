package com.snippetvault.collection;

import java.time.Instant;

public record CollectionResponse(
        String id,
        String name,
        String description,
        String ownerId,
        Instant createdAt,
        Instant updatedAt
) {}