package com.snippetvault.snippet;

import com.snippetvault.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/snippets")
@RequiredArgsConstructor
public class SnippetController {

    private final SnippetService snippetService;

    @PostMapping
    public ResponseEntity<SnippetResponse> create(
            @Valid @RequestBody CreateSnippetRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(snippetService.create(request, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SnippetResponse> getById(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(snippetService.getById(id, user));
    }

    @GetMapping("/me")
    public ResponseEntity<Page<SnippetResponse>> getMySnippets(
            @AuthenticationPrincipal AuthenticatedUser user,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(snippetService.getMySnippets(user, pageable));
    }

    @GetMapping("/search/language/{language}")
    public ResponseEntity<Page<SnippetResponse>> byLanguage(
            @PathVariable String language,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(snippetService.searchByLanguage(language, pageable));
    }

    @GetMapping("/search/tag/{tag}")
    public ResponseEntity<Page<SnippetResponse>> byTag(
            @PathVariable String tag,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(snippetService.searchByTag(tag, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SnippetResponse>> fullTextSearch(
            @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(snippetService.fullTextSearch(q, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SnippetResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateSnippetRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(snippetService.update(id, request, user));
    }

    @PatchMapping("/{id}/content")
    public ResponseEntity<SnippetResponse> patchContent(
            @PathVariable String id,
            @Valid @RequestBody PatchContentRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(snippetService.patchContent(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        snippetService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/fork")
    public ResponseEntity<SnippetResponse> fork(
            @PathVariable String id,
            @AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(snippetService.fork(id, user));
    }
}