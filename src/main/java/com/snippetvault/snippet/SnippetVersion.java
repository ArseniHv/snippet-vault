package com.snippetvault.snippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnippetVersion {
    private int versionNumber;
    private String content;
    private Instant savedAt;
}