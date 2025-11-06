package com.regression.framework.models;

import java.time.LocalDateTime;

public class TestResult {
    private String testName;
    private String status; // PASSED, FAILED, SKIPPED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private String screenshotPath;
    // Add these fields:
    private String screenshotBase64;  // Store screenshot as base64 for DB
    private String failureType;       // "AssertionError", "Timeout", etc.
    private String browserLogs;       // Browser console logs
    private String networkLogs;       // Network requests during failure
    private String stackTrace;
    private Long executionTime; // in milliseconds

    public TestResult() {}

    public TestResult(String testName, String status) {
        this.testName = testName;
        this.status = status;
        this.startTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getScreenshotPath() { return screenshotPath; }
    public void setScreenshotPath(String screenshotPath) { this.screenshotPath = screenshotPath; }

    public String getStackTrace() { return stackTrace; }
    public void setStackTrace(String stackTrace) { this.stackTrace = stackTrace; }

    public Long getExecutionTime() { return executionTime; }
    public void setExecutionTime(Long executionTime) { this.executionTime = executionTime; }
}