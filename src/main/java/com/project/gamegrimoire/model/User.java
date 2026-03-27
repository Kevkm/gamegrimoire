package com.project.gamegrimoire.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String displayname;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PlatformConnection> platformConnections = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Game> games = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id;}
    public void setId(Long id) {
        this.id = id;
    }   

    public String getEmail() { return email;}   
    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayname() { return displayname;}
    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public LocalDateTime getCreatedAt() { return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<PlatformConnection> getPlatformConnections() { return platformConnections;}
    public void setPlatformConnections(List<PlatformConnection> platformConnections) {
        this.platformConnections = platformConnections;
    }

    public List<Game> getGames() {return games;}
    public void setGames(List<Game> games) {
        this.games = games;
    }
}

