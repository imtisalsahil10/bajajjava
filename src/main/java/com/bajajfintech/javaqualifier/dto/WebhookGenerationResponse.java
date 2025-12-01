package com.bajajfintech.javaqualifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebhookGenerationResponse {
    
    @JsonProperty("webhook")
    private String webhook;
    
    @JsonProperty("accessToken")
    private String accessToken;

    public WebhookGenerationResponse() {
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
