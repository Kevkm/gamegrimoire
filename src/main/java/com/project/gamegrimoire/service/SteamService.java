package com.project.gamegrimoire.service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.gamegrimoire.Repository.GameRepository;
import com.project.gamegrimoire.dto.steam.SteamOwnedGamesResponse;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.User;

@Service
public class SteamService {
    @Value("${gaming.steam.api-key}")
    private String steamApiKey;

    private final GameRepository gameRepository;
    private final RestTemplate restTemplate;

    private static final String STEAM_API_BASE = "http://api.steampowered.com";
    public SteamService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
        this.restTemplate = new RestTemplate();     
    }
    public List<Game> fetchAndSaveGames(User user, String steamId) {
        String url = String.format(
            "%s/IPlayerService/GetOwnedGames/v0001/?key=%s&steamid=%s&include_appinfo=true&format=json",
            STEAM_API_BASE, steamApiKey, steamId
        );

        SteamOwnedGamesResponse response = restTemplate.getForObject(url, SteamOwnedGamesResponse.class);

        if (response == null || response.getResponse() == null || response.getResponse().getGames() == null) {
            return new ArrayList<>();
        }

        List<Game> games = new ArrayList<>();
        for (SteamOwnedGamesResponse.SteamGame steamGame : response.getResponse().getGames()) {
            //Check if the game already exists for the user and platform
            List<Game> existing = gameRepository.findByUserAndPlatformGameId(
            user, String.valueOf(steamGame.getAppid())
        );
        
        Game game = existing.isEmpty() ? new Game() : existing.get(0);
        game.setUser(user);
        game.setPlatform(Game.Platform.STEAM);
        game.setPlatformGameId(String.valueOf(steamGame.getAppid()));
        game.setName(steamGame.getName());
        game.setPlaytimeMinutes(steamGame.getPlaytimeForever());

        if (steamGame.getImgIconUrl() != null && !steamGame.getImgIconUrl().isEmpty()) {
            game.setIconUrl(String.format(
                "http://media.steampowered.com/steamcommunity/public/images/apps/%d/%s.jpg",
                steamGame.getAppid(), steamGame.getImgIconUrl()
            ));
        }

        if (steamGame.getRtimeLastPlayed() != null && steamGame.getRtimeLastPlayed() > 0) {
            game.setLastPlayed(LocalDateTime.ofInstant(
                Instant.ofEpochSecond(steamGame.getRtimeLastPlayed()),
                ZoneId.systemDefault()
            ));
        }

        games.add(game);
    }

    return gameRepository.saveAll(games);
}
}
