package com.project.gamegrimoire.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.gamegrimoire.model.Game.Platform;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "platformconnections")
public class PlatformConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Game.Platform platform;

    private String platformUserId;
    private String platformUsername;
    private String PlatformEmail;
    private LocalDateTime connectedAt;
    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        connectedAt = LocalDateTime.now();
    }
    @Column(columnDefinition = "TEXT")
    private String accessToken;
    // Getters and setters
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public Long getId() { return id;}
    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() { return user;}
    public void setUser(User user) {
        this.user = user;
    }

    public Platform getPlatform() { return platform;}
    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getPlatformUserId() { return platformUserId;}
    public void setPlatformUserId(String platformUserId) {
        this.platformUserId = platformUserId;
    }

    public String getPlatformUsername() { return platformUsername;}
    public void setPlatformUsername(String platformUsername) {
        this.platformUsername = platformUsername;
    }
    public String getPlatformEmail() { return PlatformEmail;}
    public void setPlatformEmail(String platformEmail) {
        PlatformEmail = platformEmail;
    }

    public LocalDateTime getConnectedAt() { return connectedAt;}
    public void setConnectedAt(LocalDateTime connectedAt) {
        this.connectedAt = connectedAt;
    }

    public boolean isActive() { return isActive;}
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
