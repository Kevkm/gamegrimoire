package com.project.gamegrimoire.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.service.XboxGameService;

@RestController
@RequestMapping("/api/xbox")
public class XboxGameController {

    private final XboxGameService xboxGameService;

    public XboxGameController(XboxGameService xboxGameService) {
        this.xboxGameService = xboxGameService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getXboxGames(@AuthenticationPrincipal String email) {
        List<Game> games = xboxGameService.syncXboxGames(email);
        return ResponseEntity.ok(games);
    }
}
