package com.snippetvault.snippet;

import jakarta.validation.constraints.NotBlank;

public record PatchContentRequest(@NotBlank String content) {}