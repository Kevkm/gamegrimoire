package com.project.gamegrimoire.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamegrimoire.Repository.UserRepository;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.User;
import com.project.gamegrimoire.service.SteamService;


@RestController
@RequestMapping("/api/steam")
public class SteamController {

    private final SteamService steamService;
    private final UserRepository userRepository;

    public SteamController(SteamService steamService, UserRepository userRepository) {
        this.steamService = steamService;
        this.userRepository = userRepository;
    }

    @GetMapping("/games")
    public ResponseEntity<?> getGames(
        @RequestParam String email,
        @RequestParam String steamId) {
        
            // Find or create user
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setDisplayname(email.split("@")[0]);
                return userRepository.save(newUser);
            });

        // Get games from SteamService and save to database
        List<Game> games = steamService.fetchAndSaveGames(user, steamId);
        return ResponseEntity.ok(games);
    }
    
}
