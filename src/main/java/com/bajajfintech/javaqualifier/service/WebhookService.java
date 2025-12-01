package com.bajajfintech.javaqualifier.service;

import com.bajajfintech.javaqualifier.dto.SolutionSubmissionRequest;
import com.bajajfintech.javaqualifier.dto.WebhookGenerationRequest;
import com.bajajfintech.javaqualifier.dto.WebhookGenerationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;

    @Value("${webhook.generate.url}")
    private String webhookGenerateUrl;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WebhookGenerationResponse generateWebhook(String name, String regNo, String email) {
        logger.info("Generating webhook for regNo: {}", regNo);
        
        WebhookGenerationRequest request = new WebhookGenerationRequest(name, regNo, email);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<WebhookGenerationRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<WebhookGenerationResponse> response = restTemplate.exchange(
                    webhookGenerateUrl,
                    HttpMethod.POST,
                    entity,
                    WebhookGenerationResponse.class
            );
            
            WebhookGenerationResponse responseBody = response.getBody();
            if (responseBody != null) {
                logger.info("Webhook generated successfully. Webhook URL: {}", responseBody.getWebhook());
            }
            
            return responseBody;
        } catch (Exception e) {
            logger.error("Error generating webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate webhook", e);
        }
    }

    public void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        logger.info("Submitting solution to webhook URL: {}", webhookUrl);
        
        SolutionSubmissionRequest request = new SolutionSubmissionRequest(sqlQuery);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        
        HttpEntity<SolutionSubmissionRequest> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            logger.info("Solution submitted successfully. Response status: {}", response.getStatusCode());
            logger.info("Response body: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Error submitting solution: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit solution", e);
        }
    }
}
