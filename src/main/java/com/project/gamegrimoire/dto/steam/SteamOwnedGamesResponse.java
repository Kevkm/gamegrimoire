package com.project.gamegrimoire.dto.steam;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class SteamOwnedGamesResponse {
    private Response response;
    public Response getResponse() {
        return response;
    }
    public void setResponse(Response response) {
        this.response = response;

    }
    public static class Response {
        @JsonProperty("game_count")
        private int gameCount;
        @JsonProperty("games")
        private List<SteamGame> games;

        public int getGameCount() {
            return gameCount;
        }
        public void setGameCount(int gameCount) {
            this.gameCount = gameCount;
        }
        public List<SteamGame> getGames() {
            return games;
        }
        public void setGames(List<SteamGame> games) {
            this.games = games;
        }
    }

    public static class SteamGame {
        private long appid;
        private String name;
        @JsonProperty("playtime_forever")
        private int playtimeForever;

        @JsonProperty("img_icon_url")
        private String imgIconUrl;

        @JsonProperty("rtime_last_played")
        private Long rtimeLastPlayed;
    
    public long getAppid() { return appid; }
        public void setAppid(long appid) { 
            this.appid = appid; }
        public String getName() { 
            return name; }
        public void setName(String name) { 
            this.name = name; }
        public int getPlaytimeForever() { 
            return playtimeForever; }
        public void setPlaytimeForever(int playtimeForever) { 
            this.playtimeForever = playtimeForever; }
        public String getImgIconUrl() { 
            return imgIconUrl; }
        public void setImgIconUrl(String imgIconUrl) { 
            this.imgIconUrl = imgIconUrl; }
        public Long getRtimeLastPlayed() { 
            return rtimeLastPlayed; }
        public void setRtimeLastPlayed(Long rtimeLastPlayed) { 
            this.rtimeLastPlayed = rtimeLastPlayed; }
    }
}
