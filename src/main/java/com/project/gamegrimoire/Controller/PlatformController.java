package com.project.gamegrimoire.Controller;

import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.PlatformConnection;
import com.project.gamegrimoire.model.User;
import com.project.gamegrimoire.Repository.PlatformConnectionRepository;
import com.project.gamegrimoire.Repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/platforms")
public class PlatformController {
    private final PlatformConnectionRepository platformConnectionRepository;
    private final UserRepository userRepository;   

    public PlatformController(PlatformConnectionRepository platformConnectionRepository, UserRepository userRepository) {
        this.platformConnectionRepository = platformConnectionRepository;
        this.userRepository = userRepository;
    }
    // Get all platform connections for the logged in user
    @GetMapping
    public ResponseEntity<?> getConnections(Authentication authentication) {
        User user = getUser(authentication);
        List<PlatformConnection> connections = platformConnectionRepository.findByUser(user);
        return ResponseEntity.ok(connections);
    }
    //Link a platform
    @PostMapping("/link")
    public ResponseEntity<?> linkPlatform(
        @RequestParam String platform,
        @RequestParam String platformUserId,
        Authentication authentication) {
        User user = getUser(authentication);
        Game.Platform p = Game.Platform.valueOf(platform.toUpperCase());
        // Check if the connection already exists
        PlatformConnection connection = platformConnectionRepository
            .findByUserAndPlatform(user, p)
            .orElse(new PlatformConnection());
        connection.setUser(user);
        connection.setPlatform(p);
        connection.setPlatformUserId(platformUserId);
        connection.setActive(true);
        platformConnectionRepository.save(connection);
        return ResponseEntity.ok("Platform linked successfully");
        }
    // Unlink a platform
    @DeleteMapping("/unlink")
    public ResponseEntity<?> unlinkPlatform(
        @RequestParam String platform,
        Authentication authentication) {

        User user = getUser(authentication);
        Game.Platform p = Game.Platform.valueOf(platform.toUpperCase());
        platformConnectionRepository
            .findByUserAndPlatform(user, p)
            .ifPresent(connection -> {
                connection.setActive(false);
                platformConnectionRepository.save(connection);
            });
        return ResponseEntity.ok("Platform unlinked successfully");
    }
    private User getUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
