package com.project.gamegrimoire.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.project.gamegrimoire.Repository.PlatformConnectionRepository;
import com.project.gamegrimoire.Repository.UserRepository;
import com.project.gamegrimoire.dto.xbox.XboxProfileResponse;
import com.project.gamegrimoire.model.Game;
import com.project.gamegrimoire.model.PlatformConnection;
import com.project.gamegrimoire.model.User;

@Service
public class XboxAuthService {

    
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PlatformConnectionRepository platformConnectionRepository;

    @Value("${gaming.xbox.client-id}")
    private String clientId;

    @Value("${gaming.xbox.client-secret}")
    private String clientSecret;

    @Value("${gaming.xbox.redirect-uri}")
    private String redirectUri;

    public XboxAuthService(PlatformConnectionRepository platformConnectionRepository,
                            UserRepository userRepository) {
            this.restTemplate = new RestTemplate();
            this.platformConnectionRepository = platformConnectionRepository;
            this.userRepository = userRepository;
    }
    
    public void handleCallback(String code, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        //Step 1: Exchange code for access token
        String msAccessToken = getMicrosoftAccessToken(code);
        //Step 2: Use Microsoft access token to get Xbox Live token
        Map<String, Object> xblResponse = getXboxLiveToken(msAccessToken);
        String xblToken = (String) xblResponse.get("Token");
        String userHash = extractUserHash(xblResponse);

        //Step 3: Exchange Xbox Live token for XSTS token
        String xstsToken = getXstsToken(xblToken);

        //Step 4: Fetch Xbox profile using XSTS token + user hash

        Map<String, Object> profile = getXboxProfile(xstsToken, userHash);
        String xuid = (String) profile.get("xuid");
        String gamertag = (String) profile.get("gamertag");

        //Step 5: Save connection in database
        PlatformConnection connection = platformConnectionRepository
        .findByUserAndPlatform(user, Game.Platform.XBOX)
                .orElse(new PlatformConnection());
                connection.setUser(user);
                connection.setPlatform(Game.Platform.XBOX);
                connection.setPlatformUserId(xuid);
                connection.setPlatformUsername(gamertag);
                connection.setAccessToken(xstsToken + "|" + userHash); // store both separated by 
                connection.setActive(true);

        platformConnectionRepository.save(connection);
    }
        // Step 1: Microsoft access token   
    private String getMicrosoftAccessToken(String code) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);
        body.add("grant_type", "authorization_code");
        body.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        @SuppressWarnings("unchecked")
        ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
            "https://login.microsoftonline.com/consumers/oauth2/v2.0/token",
            request, (Class<Map<String, Object>>) (Class<?>) Map.class);
        if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
            throw new RuntimeException("Failed to obtain Microsoft access token");
        }

        return (String) response.getBody().get("access_token");
    }

    // Step 2: Xbox Live(XBL) token
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String,Object> getXboxLiveToken(String msAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        Map<String, Object> properties = new java.util.HashMap<>();
            properties.put("AuthMethod", "RPS");
            properties.put("SiteName", "user.auth.xboxlive.com");
            properties.put("RpsTicket", "d=" + msAccessToken);

        Map<String, Object> body = Map.of(
            "Properties", properties,
            "RelyingParty", "http://auth.xboxlive.com",
            "TokenType", "JWT"
        );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://user.auth.xboxlive.com/user/authenticate",
                request, Map.class);

            if (response.getBody() == null) {
                throw new RuntimeException("Failed to obtain Xbox Live token");
            }
                return response.getBody();
    }

    // Step 3: XSTS token
    @SuppressWarnings("rawtypes")
    private String getXstsToken(String xblToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        Map<String, Object> body = Map.of(
            "Properties", Map.of(
                "SandboxId", "RETAIL",
                "UserTokens", List.of(xblToken)
            ),
            "RelyingParty", "http://xboxlive.com",
                "TokenType", "JWT"
        );

         HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://xsts.auth.xboxlive.com/xsts/authorize",
                request, Map.class);

        if (response.getBody() == null || !response.getBody().containsKey("Token")) {
            throw new RuntimeException("Failed to obtain XSTS token");
        }

        // XSTS error 2148916233 = no Xbox profile on this Microsoft account

         if (response.getBody().containsKey("XErr")) {
            Long xerr = (Long) response.getBody().get("XErr");
            if (xerr == 2148916233L) {
                throw new RuntimeException("This Microsoft account has no Xbox profile.");
            }
            throw new RuntimeException("XSTS error: " + xerr);
        }

        return (String) response.getBody().get("Token");
    
    }
    // Step 4: Xbox profile
@SuppressWarnings("rawtypes")
private Map<String, Object> getXboxProfile(String xstsToken, String userHash) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "XBL3.0 x=" + userHash + ";" + xstsToken);
        headers.set("x-xbl-contract-version", "3");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://profile.xboxlive.com/users/me/profile/settings?settings=Gamertag",
                HttpMethod.GET, request, Map.class);

        if (response.getBody() == null) {
            throw new RuntimeException("Failed to fetch Xbox profile");
        }

        // Parse xuid and gamertag from response
        @SuppressWarnings("unchecked")
        Map<String, Object> profileUser = extractProfileUser(response.getBody());
        return profileUser;
    }

    //  Helpers 

    @SuppressWarnings("unchecked")
    private String extractUserHash(Map<String, Object> xblResponse) {
        Map<String, Object> displayClaims = (Map<String, Object>) xblResponse.get("DisplayClaims");
        List<Map<String, Object>> xui = (List<Map<String, Object>>) displayClaims.get("xui");
        return (String) xui.get(0).get("uhs");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractProfileUser(Map<String, Object> response) {
        List<Map<String, Object>> users = (List<Map<String, Object>>) response.get("profileUsers");
        Map<String, Object> profileUser = users.get(0);

        String xuid = (String) profileUser.get("id");
        List<Map<String, Object>> settings = (List<Map<String, Object>>) profileUser.get("settings");
        String gamertag = (String) settings.get(0).get("value");

        return Map.of("xuid", xuid, "gamertag", gamertag);
    }

    // Status / Disconnect 

    public boolean isLinked(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return platformConnectionRepository.existsByUserAndPlatform(user, Game.Platform.XBOX);
    }

    public void disconnect(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        platformConnectionRepository.findByUserAndPlatform(user, Game.Platform.XBOX)
                .ifPresent(connection -> {
                    connection.setActive(false);
                    platformConnectionRepository.save(connection);
                });

    }
    public XboxProfileResponse getProfile(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found: " + email));

    return platformConnectionRepository.findByUserAndPlatform(user, Game.Platform.XBOX)
            .filter(connection -> connection.isActive())
            .map(connection -> new XboxProfileResponse(
                    true,
                    connection.getPlatformUserId(),
                    connection.getPlatformUsername(),
                    connection.getConnectedAt().toString()
            ))
            .orElse(new XboxProfileResponse(false, null, null, null));
}
}
