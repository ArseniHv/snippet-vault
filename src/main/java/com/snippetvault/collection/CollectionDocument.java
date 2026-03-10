package com.snippetvault.collection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "collections")
public class CollectionDocument {

    @Id
    private String id;

    private String name;

    private String description;

    @Indexed
    private String ownerId;

    private Instant createdAt;
    private Instant updatedAt;
}