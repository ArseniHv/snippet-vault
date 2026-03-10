package com.snippetvault.auth;

public record AuthResponse(String token, String userId, String username) {}