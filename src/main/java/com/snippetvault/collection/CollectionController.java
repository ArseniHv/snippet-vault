package com.snippetvault.collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.snippetvault.auth.AuthenticatedUser;
import com.snippetvault.snippet.SnippetResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<CollectionResponse> create(
            @Valid @RequestBody CreateCollectionRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(collectionService.create(request, user));
    }

    @GetMapping
    public ResponseEntity<Page<CollectionResponse>> getMyCollections(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(collectionService.getMyCollections(user, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponse> getById(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(collectionService.getById(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCollectionRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(collectionService.update(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        collectionService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/snippets")
    public ResponseEntity<Page<SnippetResponse>> getSnippets(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(collectionService.getSnippetsInCollection(id, user, pageable));
    }
}