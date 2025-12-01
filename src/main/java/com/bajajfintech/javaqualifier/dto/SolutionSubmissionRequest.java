package com.bajajfintech.javaqualifier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SolutionSubmissionRequest {
    
    @JsonProperty("finalQuery")
    private String finalQuery;

    public SolutionSubmissionRequest() {
    }

    public SolutionSubmissionRequest(String finalQuery) {
        this.finalQuery = finalQuery;
    }

    public String getFinalQuery() {
        return finalQuery;
    }

    public void setFinalQuery(String finalQuery) {
        this.finalQuery = finalQuery;
    }
}
