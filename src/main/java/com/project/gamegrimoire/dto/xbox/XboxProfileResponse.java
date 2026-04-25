package com.project.gamegrimoire.dto.xbox;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XboxProfileResponse {
    private boolean linked;
    private String xuid;
    private String gamertag;

    @JsonProperty("connected_at")
    private String connectedAt;

    public XboxProfileResponse() {}

    public XboxProfileResponse(boolean linked, String xuid, String gamertag, String connectedAt) {
        this.linked = linked;
        this.xuid = xuid;
        this.gamertag = gamertag;
        this.connectedAt = connectedAt;
    }
    public boolean isLinked() { return linked; }
    public void setLinked(boolean linked) { this.linked = linked; }

    public String getXuid() { return xuid; }
    public void setXuid(String xuid) { this.xuid = xuid; }

    public String getGamertag() { return gamertag; }
    public void setGamertag(String gamertag) { this.gamertag = gamertag; }

    public String getConnectedAt() { return connectedAt; }
    public void setConnectedAt(String connectedAt) { this.connectedAt = connectedAt; }
}
