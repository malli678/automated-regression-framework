package com.regression.framework.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_runs")
public class TestRunEntity {
    @Id
    private String runId;

    private String testSuiteName;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reportUrl;
    private Integer totalTests;
    private Integer passedTests;
    private Integer failedTests;

    @Column(length = 1000)
    private String tags;

    @Column(length = 2000)
    private String environmentVariables;

    @Column(length = 4000)
    private String screenshotPaths; // Store comma-separated screenshot paths

    @Column(length = 1000)
    private String failureDetails; // Store failure summary

    // No-args constructor
    public TestRunEntity() {}

    // Constructor with runId, testSuiteName, status
    public TestRunEntity(String runId, String testSuiteName, String status) {
        this.runId = runId;
        this.testSuiteName = testSuiteName;
        this.status = status;
        this.startTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getTestSuiteName() { return testSuiteName; }
    public void setTestSuiteName(String testSuiteName) { this.testSuiteName = testSuiteName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getReportUrl() { return reportUrl; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }

    public Integer getTotalTests() { return totalTests; }
    public void setTotalTests(Integer totalTests) { this.totalTests = totalTests; }

    public Integer getPassedTests() { return passedTests; }
    public void setPassedTests(Integer passedTests) { this.passedTests = passedTests; }

    public Integer getFailedTests() { return failedTests; }
    public void setFailedTests(Integer failedTests) { this.failedTests = failedTests; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getEnvironmentVariables() { return environmentVariables; }
    public void setEnvironmentVariables(String environmentVariables) { this.environmentVariables = environmentVariables; }
    public String getScreenshotPaths() {
        return screenshotPaths;
    }

    public void setScreenshotPaths(String screenshotPaths) {
        this.screenshotPaths = screenshotPaths;
    }

    public String getFailureDetails() {
        return failureDetails;
    }

    public void setFailureDetails(String failureDetails) {
        this.failureDetails = failureDetails;
    }
}