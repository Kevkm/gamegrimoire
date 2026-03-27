package com.project.gamegrimoire.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.project.gamegrimoire.model.Game.Platform;

@Entity
@Table(name = "platformconnections")
public class Platformconnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // Getters and setters
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

    public boolean IsActive() { return isActive;}
    public void setActive(boolean active) {
        this.isActive = active;
    }
}
