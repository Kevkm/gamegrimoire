package com.project.gamegrimoire.Controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.gamegrimoire.dto.xbox.XboxProfileResponse;
import com.project.gamegrimoire.service.XboxAuthService;

@RestController
@RequestMapping("/api/xbox")
public class XboxAuthController {
    
    private static final Logger log = LoggerFactory.getLogger(XboxAuthController.class);
    private final XboxAuthService xboxAuthService;

    @Value("${gaming.xbox.client-id}")
    private String clientId;    

    @Value("${gaming.xbox.redirect-uri}")
    private String redirectUri;

    @Value("${gaming.xbox.scope}")
    private String scope;

    public XboxAuthController(XboxAuthService xboxAuthService) {
        this.xboxAuthService = xboxAuthService;
    }

    @GetMapping("/connect")
    public ResponseEntity<?> connect(@AuthenticationPrincipal String email) {
    String state = java.util.Base64.getEncoder()
            .encodeToString(email.getBytes());
    
    String authUrl = "https://login.microsoftonline.com/consumers/oauth2/v2.0/authorize"
            + "?client_id=" + clientId
            + "&response_type=code"
            + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
            + "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8)
            + "&response_mode=query"
            + "&state=" + state;
    return ResponseEntity.ok(Map.of("authUrl", authUrl));
}

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String error,
        @RequestParam(required = false) String state) {

    if (error != null || code == null || state == null) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/dashboard?xbox=error")
                .build();
    }

    try {
        String email = new String(java.util.Base64.getDecoder().decode(state));
        xboxAuthService.handleCallback(code, email);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/dashboard?xbox=linked")
                .build();
    } catch (Exception e) {
        log.error("Xbox callback error: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:5173/dashboard?xbox=error")
                .build();
    }
}

        @GetMapping("/status")
    public ResponseEntity<XboxProfileResponse> status(@AuthenticationPrincipal String email) {
        XboxProfileResponse profile = xboxAuthService.getProfile(email);
        return ResponseEntity.ok(profile);
    }

         @DeleteMapping("/disconnect")
    public ResponseEntity<?> disconnect(@AuthenticationPrincipal String email) {
        xboxAuthService.disconnect(email);
        return ResponseEntity.ok(Map.of("message", "Xbox account disconnected."));
    }
    }