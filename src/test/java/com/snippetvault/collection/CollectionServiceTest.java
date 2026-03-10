package com.snippetvault.collection;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import com.snippetvault.auth.AuthenticatedUser;
import com.snippetvault.snippet.SnippetMapper;
import com.snippetvault.snippet.SnippetRepository;

@ExtendWith(MockitoExtension.class)
class CollectionServiceTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private SnippetRepository snippetRepository;

    @Mock
    private SnippetMapper snippetMapper;

    @InjectMocks
    private CollectionService collectionService;

    private AuthenticatedUser owner;
    private AuthenticatedUser otherUser;
    private CollectionDocument collection;

    @BeforeEach
    void setUp() {
        owner = new AuthenticatedUser("user1", "alice");
        otherUser = new AuthenticatedUser("user2", "bob");

        collection = CollectionDocument.builder()
                .id("col1")
                .name("My Snippets")
                .description("A test collection")
                .ownerId("user1")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void create_savesCollectionWithOwner() {
        when(collectionRepository.save(any())).thenReturn(collection);
        when(collectionMapper.toResponse(any())).thenReturn(mock(CollectionResponse.class));

        collectionService.create(new CreateCollectionRequest("My Snippets", "A test collection"), owner);

        verify(collectionRepository).save(argThat(c -> c.getOwnerId().equals("user1")));
    }

    @Test
    void getById_byNonOwner_throwsForbidden() {
        when(collectionRepository.findById("col1")).thenReturn(Optional.of(collection));

        assertThatThrownBy(() -> collectionService.getById("col1", otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void update_byOwner_updatesFields() {
        when(collectionRepository.findById("col1")).thenReturn(Optional.of(collection));
        when(collectionRepository.save(any())).thenReturn(collection);
        when(collectionMapper.toResponse(any())).thenReturn(mock(CollectionResponse.class));

        collectionService.update("col1", new UpdateCollectionRequest("New Name", null), owner);

        verify(collectionRepository).save(argThat(c -> c.getName().equals("New Name")));
    }

    @Test
    void delete_byNonOwner_throwsForbidden() {
        when(collectionRepository.findById("col1")).thenReturn(Optional.of(collection));

        assertThatThrownBy(() -> collectionService.delete("col1", otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void delete_byOwner_deletesCollection() {
        when(collectionRepository.findById("col1")).thenReturn(Optional.of(collection));

        collectionService.delete("col1", owner);

        verify(collectionRepository).delete(collection);
    }
}