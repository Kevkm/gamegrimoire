package com.project.gamegrimoire.dto.auth;

public class AuthResponse {
    @SuppressWarnings("FieldMayBeFinal")
    private String token;
    @SuppressWarnings("FieldMayBeFinal")
    private String username;
    @SuppressWarnings("FieldMayBeFinal")
    private String email;
    @SuppressWarnings("FieldMayBeFinal")
    private Long userId;

    public AuthResponse(String token, String username, String email, Long userId) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.userId = userId;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getUserId() { return userId; }
}