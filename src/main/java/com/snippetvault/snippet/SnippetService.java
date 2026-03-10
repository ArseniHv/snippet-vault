package com.snippetvault.snippet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.snippetvault.auth.AuthenticatedUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SnippetService {

    private final SnippetRepository snippetRepository;
    private final SnippetMapper snippetMapper;

    public SnippetResponse create(CreateSnippetRequest request, AuthenticatedUser user) {
        SnippetDocument doc = SnippetDocument.builder()
                .title(request.title())
                .content(request.content())
                .programmingLanguage(request.language())
                .description(request.description())
                .tags(request.tags() != null ? request.tags() : List.of())
                .author(new SnippetAuthor(user.userId(), user.username()))
                .collectionId(request.collectionId())
                .isPublic(request.isPublic())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return snippetMapper.toResponse(snippetRepository.save(doc));
    }

    public SnippetResponse getById(String id, AuthenticatedUser user) {
        SnippetDocument doc = findOrThrow(id);

        if (!doc.isPublic()) {
            requireOwner(doc, user);
        }

        doc.setViewCount(doc.getViewCount() + 1);
        snippetRepository.save(doc);

        return snippetMapper.toResponse(doc);
    }

    public Page<SnippetResponse> getMySnippets(AuthenticatedUser user, Pageable pageable) {
        return snippetRepository.findByAuthorUserId(user.userId(), pageable)
                .map(snippetMapper::toResponse);
    }

    public Page<SnippetResponse> searchByLanguage(String language, Pageable pageable) {
        return snippetRepository.findByProgrammingLanguageAndIsPublicTrue(language, pageable)
                .map(snippetMapper::toResponse);
    }

    public Page<SnippetResponse> searchByTag(String tag, Pageable pageable) {
        return snippetRepository.findByTagsContainingAndIsPublicTrue(tag, pageable)
                .map(snippetMapper::toResponse);
    }

    public Page<SnippetResponse> fullTextSearch(String query, Pageable pageable) {
        return snippetRepository.fullTextSearch(query, pageable)
                .map(snippetMapper::toResponse);
    }

    public SnippetResponse update(String id, UpdateSnippetRequest request, AuthenticatedUser user) {
        SnippetDocument doc = findOrThrow(id);
        requireOwner(doc, user);

        if (request.title() != null) doc.setTitle(request.title());
        if (request.language() != null) doc.setProgrammingLanguage(request.language());
        if (request.description() != null) doc.setDescription(request.description());
        if (request.tags() != null) doc.setTags(request.tags());
        if (request.collectionId() != null) doc.setCollectionId(request.collectionId());
        if (request.isPublic() != null) doc.setPublic(request.isPublic());
        doc.setUpdatedAt(Instant.now());

        return snippetMapper.toResponse(snippetRepository.save(doc));
    }

    public SnippetResponse patchContent(String id, PatchContentRequest request, AuthenticatedUser user) {
        SnippetDocument doc = findOrThrow(id);
        requireOwner(doc, user);

        SnippetVersion previousVersion = SnippetVersion.builder()
                .versionNumber(doc.getVersionHistory().size() + 1)
                .content(doc.getContent())
                .savedAt(doc.getUpdatedAt())
                .build();

        List<SnippetVersion> history = new ArrayList<>(doc.getVersionHistory());
        history.add(previousVersion);

        doc.setVersionHistory(history);
        doc.setContent(request.content());
        doc.setUpdatedAt(Instant.now());

        return snippetMapper.toResponse(snippetRepository.save(doc));
    }

    public void delete(String id, AuthenticatedUser user) {
        SnippetDocument doc = findOrThrow(id);
        requireOwner(doc, user);
        snippetRepository.delete(doc);
    }

    public SnippetResponse fork(String id, AuthenticatedUser user) {
        SnippetDocument original = findOrThrow(id);

        if (!original.isPublic()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot fork a private snippet");
        }

        original.setForkCount(original.getForkCount() + 1);
        snippetRepository.save(original);

        SnippetDocument forked = SnippetDocument.builder()
                .title("Fork of " + original.getTitle())
                .content(original.getContent())
                .programmingLanguage(original.getProgrammingLanguage())
                .description(original.getDescription())
                .tags(new ArrayList<>(original.getTags()))
                .author(new SnippetAuthor(user.userId(), user.username()))
                .isPublic(false)
                .forkedFrom(original.getId())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        return snippetMapper.toResponse(snippetRepository.save(forked));
    }

    private SnippetDocument findOrThrow(String id) {
        return snippetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Snippet not found"));
    }

    private void requireOwner(SnippetDocument doc, AuthenticatedUser user) {
        if (user == null || !doc.getAuthor().getUserId().equals(user.userId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}