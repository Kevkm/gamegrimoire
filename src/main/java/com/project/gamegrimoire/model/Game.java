package com.project.gamegrimoire.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore
    @ManyToOne (fetch = FetchType.LAZY)

    private User user;

    @Enumerated(EnumType.STRING)
    private Platform platform;

    private String platformGameId;
    private String name;
    private String iconUrl;
    private Integer playtimeMinutes;
    private Integer achievmentsEarned;
    private Integer achievmentsTotal;
    private LocalDateTime lastPlayed;

    public enum Platform {
        STEAM, EPIC_GAMES, XBOX
    }

    // Getters and setters
    public Long getId() { return id; }     
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
    public String getPlatformGameId() { return platformGameId;}
    public void setPlatformGameId(String platformGameId) {
        this.platformGameId = platformGameId;
    }
    public String getName() { return name;}
    public void setName(String name) {
        this.name = name;
    }
    public String getIconUrl() { return iconUrl;}
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }   
    public Integer getPlaytimeMinutes() { return playtimeMinutes;}
    public void setPlaytimeMinutes(Integer playtimeMinutes) {
        this.playtimeMinutes = playtimeMinutes;
    }
    public Integer getAchievmentsEarned() { return achievmentsEarned;}
    public void setAchievmentsEarned(Integer achievmentsEarned) {
        this.achievmentsEarned = achievmentsEarned;
    }
    public Integer getAchievmentsTotal() { return achievmentsTotal;}
    public void setAchievmentsTotal(Integer achievmentsTotal) {
        this.achievmentsTotal = achievmentsTotal;
    }
    public LocalDateTime getLastPlayed() { return lastPlayed;}
    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
