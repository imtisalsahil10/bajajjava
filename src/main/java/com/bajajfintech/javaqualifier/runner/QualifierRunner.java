package com.bajajfintech.javaqualifier.runner;

import com.bajajfintech.javaqualifier.dto.WebhookGenerationResponse;
import com.bajajfintech.javaqualifier.service.SqlQueryService;
import com.bajajfintech.javaqualifier.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class QualifierRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(QualifierRunner.class);

    private final WebhookService webhookService;
    private final SqlQueryService sqlQueryService;

    @Value("${registration.name}")
    private String name;

    @Value("${registration.regNo}")
    private String regNo;

    @Value("${registration.email}")
    private String email;

    public QualifierRunner(WebhookService webhookService, SqlQueryService sqlQueryService) {
        this.webhookService = webhookService;
        this.sqlQueryService = sqlQueryService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("============================================");
        logger.info("Starting Java Qualifier Application");
        logger.info("Registration Number: {}", regNo);
        logger.info("============================================");

        try {
            // Step 1: Generate webhook
            logger.info("Step 1: Generating webhook...");
            WebhookGenerationResponse webhookResponse = webhookService.generateWebhook(name, regNo, email);

            if (webhookResponse == null || webhookResponse.getWebhook() == null || webhookResponse.getAccessToken() == null) {
                logger.error("Failed to generate webhook. Response is null or missing required fields.");
                return;
            }

            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();

            logger.info("Webhook URL received: {}", webhookUrl);
            logger.info("Access token received: {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");

            // Step 2: Determine question and generate SQL query
            logger.info("Step 2: Determining question based on registration number...");
            boolean isQuestionOne = sqlQueryService.isQuestionOne(regNo);
            String questionNumber = isQuestionOne ? "1" : "2";
            logger.info("Question determined: Question {}", questionNumber);

            logger.info("Step 3: Generating SQL query...");
            String sqlQuery = sqlQueryService.getSqlQuery(regNo);
            logger.info("SQL Query generated:");
            logger.info("----------------------------------------");
            logger.info(sqlQuery);
            logger.info("----------------------------------------");

            // Step 3: Submit solution
            logger.info("Step 4: Submitting solution to webhook URL...");
            webhookService.submitSolution(webhookUrl, accessToken, sqlQuery);

            logger.info("============================================");
            logger.info("Application completed successfully!");
            logger.info("============================================");

        } catch (Exception e) {
            logger.error("Error during application execution: {}", e.getMessage(), e);
            throw e;
        }
    }
}
