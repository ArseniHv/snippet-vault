package com.snippetvault.collection;

import jakarta.validation.constraints.Size;

public record UpdateCollectionRequest(
        @Size(max = 100) String name,
        String description
) {}