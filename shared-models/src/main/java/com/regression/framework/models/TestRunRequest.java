package com.regression.framework.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

public class TestRunRequest {

    @NotBlank(message = "Test suite name is required")
    private String testSuiteName;

    private List<String> tags;
    private Map<String, String> environmentVariables;

    @NotNull(message = "Execution type is required")
    private ExecutionType executionType;

    private Integer retryCount = 1;
    private String browserType = "chrome";

    public enum ExecutionType {
        WEB, API, ALL
    }

    // Constructors
    public TestRunRequest() {}

    public TestRunRequest(String testSuiteName, List<String> tags,
                          Map<String, String> environmentVariables,
                          ExecutionType executionType) {
        this.testSuiteName = testSuiteName;
        this.tags = tags;
        this.environmentVariables = environmentVariables;
        this.executionType = executionType;
    }

    // Getters and Setters
    public String getTestSuiteName() { return testSuiteName; }
    public void setTestSuiteName(String testSuiteName) { this.testSuiteName = testSuiteName; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Map<String, String> getEnvironmentVariables() { return environmentVariables; }
    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public ExecutionType getExecutionType() { return executionType; }
    public void setExecutionType(ExecutionType executionType) { this.executionType = executionType; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public String getBrowserType() { return browserType; }
    public void setBrowserType(String browserType) { this.browserType = browserType; }
}