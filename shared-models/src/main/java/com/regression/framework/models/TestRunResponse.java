package com.regression.framework.models;

import java.time.LocalDateTime;

public class TestRunResponse {
    private String runId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String message;
    private String reportUrl;
    private Integer totalTests;
    private Integer passedTests;
    private Integer failedTests;

    // No-args constructor
    public TestRunResponse() {}

    // Constructor with runId, status, message
    public TestRunResponse(String runId, String status, String message) {
        this.runId = runId;
        this.status = status;
        this.message = message;
        this.startTime = LocalDateTime.now();
    }

    // All-args constructor
    public TestRunResponse(String runId, String status, LocalDateTime startTime,
                           LocalDateTime endTime, String message, String reportUrl,
                           Integer totalTests, Integer passedTests, Integer failedTests) {
        this.runId = runId;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.message = message;
        this.reportUrl = reportUrl;
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
    }

    // Getters and Setters
    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }

    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }

    public Integer getPassedTests() { return passedTests; }
    public void setPassedTests(Integer passedTests) { this.passedTests = passedTests; }

    public Integer getFailedTests() { return failedTests; }
    public void setFailedTests(Integer failedTests) { this.failedTests = failedTests; }
}