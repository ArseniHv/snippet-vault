package com.snippetvault.snippet;

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

@ExtendWith(MockitoExtension.class)
class SnippetServiceTest {

    @Mock
    private SnippetRepository snippetRepository;

    @Mock
    private SnippetMapper snippetMapper;

    @InjectMocks
    private SnippetService snippetService;

    private AuthenticatedUser owner;
    private AuthenticatedUser otherUser;
    private SnippetDocument publicSnippet;
    private SnippetDocument privateSnippet;

    @BeforeEach
    void setUp() {
        owner = new AuthenticatedUser("user1", "alice");
        otherUser = new AuthenticatedUser("user2", "bob");

        publicSnippet = SnippetDocument.builder()
                .id("snip1")
                .title("Test")
                .content("console.log('hello')")
                .programmingLanguage("javascript")
                .author(new SnippetAuthor("user1", "alice"))
                .isPublic(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        privateSnippet = SnippetDocument.builder()
                .id("snip2")
                .title("Private")
                .content("secret")
                .programmingLanguage("bash")
                .author(new SnippetAuthor("user1", "alice"))
                .isPublic(false)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void getById_publicSnippet_incrementsViewCount() {
        when(snippetRepository.findById("snip1")).thenReturn(Optional.of(publicSnippet));
        when(snippetRepository.save(any())).thenReturn(publicSnippet);
        when(snippetMapper.toResponse(any())).thenReturn(mock(SnippetResponse.class));

        snippetService.getById("snip1", otherUser);

        verify(snippetRepository).save(argThat(s -> s.getViewCount() == 1));
    }

    @Test
    void getById_privateSnippet_forbiddenForOtherUser() {
        when(snippetRepository.findById("snip2")).thenReturn(Optional.of(privateSnippet));

        assertThatThrownBy(() -> snippetService.getById("snip2", otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }

    @Test
    void fork_privateSnippet_throwsForbidden() {
        when(snippetRepository.findById("snip2")).thenReturn(Optional.of(privateSnippet));

        assertThatThrownBy(() -> snippetService.fork("snip2", otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Cannot fork a private snippet");
    }

    @Test
    void patchContent_savesVersionHistory() {
        when(snippetRepository.findById("snip1")).thenReturn(Optional.of(publicSnippet));
        when(snippetRepository.save(any())).thenReturn(publicSnippet);
        when(snippetMapper.toResponse(any())).thenReturn(mock(SnippetResponse.class));

        snippetService.patchContent("snip1", new PatchContentRequest("new content"), owner);

        verify(snippetRepository).save(argThat(s ->
                s.getVersionHistory().size() == 1 &&
                s.getVersionHistory().get(0).getContent().equals("console.log('hello')")
        ));
    }

    @Test
    void delete_byNonOwner_throwsForbidden() {
        when(snippetRepository.findById("snip1")).thenReturn(Optional.of(publicSnippet));

        assertThatThrownBy(() -> snippetService.delete("snip1", otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Access denied");
    }
}