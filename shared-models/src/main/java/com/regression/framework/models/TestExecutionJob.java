package com.regression.framework.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class TestExecutionJob {
    private String jobId;
    private String runId;
    private TestRunRequest testRunRequest;
    private LocalDateTime createdAt;
    private String status;
    private int retryCount = 0;
    private List<TestResult> testResults;

    public TestExecutionJob() {
        this.createdAt = LocalDateTime.now();
        this.status = "QUEUED";
    }

    public TestExecutionJob(String runId, TestRunRequest testRunRequest) {
        this();
        this.jobId = "JOB_" + System.currentTimeMillis() + "_" + runId;
        this.runId = runId;
        this.testRunRequest = testRunRequest;
    }

    // Getters and Setters
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public TestRunRequest getTestRunRequest() { return testRunRequest; }
    public void setTestRunRequest(TestRunRequest testRunRequest) {
        this.testRunRequest = testRunRequest;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }

    public List<TestResult> getTestResults() { return testResults; }
    public void setTestResults(List<TestResult> testResults) { this.testResults = testResults; }
}