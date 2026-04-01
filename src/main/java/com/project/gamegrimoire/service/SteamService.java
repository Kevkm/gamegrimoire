package com.project.gamegrimoire.service;


import com.project.gamegrimoire.dto.steam.SteamOwnedGamesResponse;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.User;
import com.project.gamegrimoire.Repository.GameRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

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
            Game game = convertToGame(steamGame, user);
            games.add(game);
        }

        return gameRepository.saveAll(games);
    }

    private Game convertToGame(SteamOwnedGamesResponse.SteamGame steamGame, User user) {
        Game game = new Game();
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

        return game;
    }
}

