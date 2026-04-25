package com.project.gamegrimoire.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.gamegrimoire.Repository.GameRepository;
import com.project.gamegrimoire.Repository.PlatformConnectionRepository;
import com.project.gamegrimoire.Repository.UserRepository;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.PlatformConnection;
import com.project.gamegrimoire.model.User;

@Service
public class XboxGameService {

    private static final Logger log = LoggerFactory.getLogger(XboxGameService.class);

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PlatformConnectionRepository platformConnectionRepository;

    public XboxGameService(UserRepository userRepository,
                           GameRepository gameRepository,
                           PlatformConnectionRepository platformConnectionRepository) {
        this.restTemplate = new RestTemplate();
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.platformConnectionRepository = platformConnectionRepository;
    }

    public List<Game> syncXboxGames(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // Get Xbox connection and extract XSTS token + userHash
        PlatformConnection connection = platformConnectionRepository
                .findByUserAndPlatform(user, Game.Platform.XBOX)
                .orElseThrow(() -> new RuntimeException("Xbox not connected"));

        String[] tokenParts = connection.getAccessToken().split("\\|");
        if (tokenParts.length != 2) {
            throw new RuntimeException("Invalid Xbox token format");
        }

        String xstsToken = tokenParts[0];
        String userHash = tokenParts[1];
        String xuid = connection.getPlatformUserId();

        // Fetch game history from Xbox API
        List<Map<String, Object>> titles = fetchXboxTitles(xstsToken, userHash, xuid);

        // Map and save games
        List<Game> games = new ArrayList<>();
        for (Map<String, Object> title : titles) {
            Game game = mapTitleToGame(title, user);
            if (game != null) {
                games.add(game);
            }
        }

        gameRepository.saveAll(games);
        log.info("Synced {} Xbox games for {}", games.size(), email);
        return games;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchXboxTitles(String xstsToken, String userHash, String xuid) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "XBL3.0 x=" + userHash + ";" + xstsToken);
        headers.set("x-xbl-contract-version", "2");
        headers.set("Accept-Language", "en-US");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = "https://titlehub.xboxlive.com/users/xuid(" + xuid + ")"
                + "/titles/titlehistory/decoration/Achievement,Image";

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, request, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to fetch Xbox titles");
        }

        Object outer = response.getBody().get("titles");
    if (outer instanceof List) {
        return (List<Map<String, Object>>) outer;
    } else if (outer instanceof Map) {
        Map<String, Object> titlesMap = (Map<String, Object>) outer;
        Object inner = titlesMap.get("titles");
        if (inner instanceof List) {
            return (List<Map<String, Object>>) inner;
        }
    }

    return new ArrayList<>();
}

    @SuppressWarnings("unchecked")
private Game mapTitleToGame(Map<String, Object> title, User user) {
    try {
        String titleId = String.valueOf(title.get("titleId"));
        String name = (String) title.get("name");

        // Skip non-game titles
        String type = (String) title.get("type");
        if ("Application".equals(type)) return null;

        Game game = gameRepository
                .findByUserAndPlatformGameId(user, titleId)
                .stream()
                .findFirst()
                .orElse(new Game());

        game.setUser(user);
        game.setPlatform(Game.Platform.XBOX);
        game.setPlatformGameId(titleId);
        game.setName(name);

        // displayImage is a direct String URL
        Object displayImage = title.get("displayImage");
        if (displayImage instanceof String string) {
            game.setIconUrl(string);
        } else if (displayImage instanceof Map) {
            game.setIconUrl((String) ((Map<String, Object>) displayImage).get("url"));
        }

        // Achievements
        Object achievementObj = title.get("achievement");
        if (achievementObj instanceof Map) {
            Map<String, Object> achievement = (Map<String, Object>) achievementObj;
            Object earned = achievement.get("currentAchievements");
            Object total = achievement.get("totalAchievements");
            if (earned instanceof Integer integer) game.setAchievmentsEarned(integer);
            if (total instanceof Integer integer) game.setAchievmentsTotal(integer);
        }

        // Last played - titleHistory is a Map
        Object titleHistoryObj = title.get("titleHistory");
        if (titleHistoryObj instanceof Map) {
            Map<String, Object> titleHistory = (Map<String, Object>) titleHistoryObj;
            String lastPlayedStr = (String) titleHistory.get("lastTimePlayed");
            if (lastPlayedStr != null) {
                game.setLastPlayed(LocalDateTime.ofInstant(
                        Instant.parse(lastPlayedStr), ZoneId.systemDefault()));
            }
        }

        return game;
    } catch (Exception e) {
        log.warn("Failed to map title: {}", e.getMessage());
        return null;
    }
}
}
