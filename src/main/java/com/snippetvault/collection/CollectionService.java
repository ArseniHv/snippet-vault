package com.snippetvault.collection;

import java.time.Instant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.snippetvault.auth.AuthenticatedUser;
import com.snippetvault.snippet.SnippetMapper;
import com.snippetvault.snippet.SnippetRepository;
import com.snippetvault.snippet.SnippetResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;
    private final SnippetRepository snippetRepository;
    private final SnippetMapper snippetMapper;

    public CollectionResponse create(CreateCollectionRequest request, AuthenticatedUser user) {
        CollectionDocument doc = CollectionDocument.builder()
                .name(request.name())
                .description(request.description())
                .ownerId(user.userId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return collectionMapper.toResponse(collectionRepository.save(doc));
    }

    public Page<CollectionResponse> getMyCollections(AuthenticatedUser user, Pageable pageable) {
        return collectionRepository.findByOwnerId(user.userId(), pageable)
                .map(collectionMapper::toResponse);
    }

    public CollectionResponse getById(String id, AuthenticatedUser user) {
        CollectionDocument doc = findOrThrow(id);
        requireOwner(doc, user);
        return collectionMapper.toResponse(doc);
    }

    public CollectionResponse update(String id, UpdateCollectionRequest request, AuthenticatedUser user) {
        CollectionDocument doc = findOrThrow(id);
        requireOwner(doc, user);

        if (request.name() != null) doc.setName(request.name());
        if (request.description() != null) doc.setDescription(request.description());
        doc.setUpdatedAt(Instant.now());

        return collectionMapper.toResponse(collectionRepository.save(doc));
    }

    public void delete(String id, AuthenticatedUser user) {
        CollectionDocument doc = findOrThrow(id);
        requireOwner(doc, user);
        collectionRepository.delete(doc);
    }

    public Page<SnippetResponse> getSnippetsInCollection(String collectionId, AuthenticatedUser user, Pageable pageable) {
        CollectionDocument collection = findOrThrow(collectionId);
        boolean isOwner = user != null && collection.getOwnerId().equals(user.userId());

        if (isOwner) {
            return snippetRepository.findByCollectionId(collectionId, pageable)
                    .map(snippetMapper::toResponse);
        }

        return snippetRepository.findByCollectionIdAndIsPublicTrue(collectionId, pageable)
                .map(snippetMapper::toResponse);
    }

    private CollectionDocument findOrThrow(String id) {
        return collectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection not found"));
    }

    private void requireOwner(CollectionDocument doc, AuthenticatedUser user) {
        if (user == null || !doc.getOwnerId().equals(user.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}