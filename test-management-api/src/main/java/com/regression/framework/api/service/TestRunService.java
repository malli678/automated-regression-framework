package com.regression.framework.api.service;

import com.regression.framework.models.TestRunRequest;
import com.regression.framework.models.TestRunResponse;
import com.regression.framework.models.TestExecutionJob;
import com.regression.framework.api.entity.TestRunEntity;
import com.regression.framework.api.repository.TestRunRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestRunService {

    private static final Logger logger = LoggerFactory.getLogger(TestRunService.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TestRunRepository testRunRepository;

    public TestRunResponse scheduleTestRun(TestRunRequest testRunRequest) {
        String runId = "RUN_" + System.currentTimeMillis();

        // Save to database
        TestRunEntity entity = new TestRunEntity();
        entity.setRunId(runId);
        entity.setTestSuiteName(testRunRequest.getTestSuiteName());
        entity.setStatus("SCHEDULED");
        entity.setStartTime(LocalDateTime.now());

        if (testRunRequest.getTags() != null) {
            entity.setTags(String.join(",", testRunRequest.getTags()));
        }

        if (testRunRequest.getEnvironmentVariables() != null) {
            entity.setEnvironmentVariables(testRunRequest.getEnvironmentVariables().toString());
        }

        testRunRepository.save(entity);

        // Create and send job to RabbitMQ
        TestExecutionJob job = new TestExecutionJob(runId, testRunRequest);
        rabbitTemplate.convertAndSend("test-execution-queue", job);

        TestRunResponse response = new TestRunResponse();
        response.setRunId(runId);
        response.setStatus("SCHEDULED");
        response.setStartTime(entity.getStartTime());
        response.setMessage("Test run scheduled successfully");

        return response;
    }

    public TestRunResponse getTestRunStatus(String runId) {
        Optional<TestRunEntity> entityOpt = testRunRepository.findById(runId);
        if (entityOpt.isPresent()) {
            return convertToResponse(entityOpt.get());
        }
        return new TestRunResponse(runId, "NOT_FOUND", "Test run not found");
    }

    public TestRunResponse stopTestRun(String runId) {
        Optional<TestRunEntity> entityOpt = testRunRepository.findById(runId);
        if (entityOpt.isPresent()) {
            TestRunEntity entity = entityOpt.get();
            entity.setStatus("STOPPED");
            entity.setEndTime(LocalDateTime.now());
            testRunRepository.save(entity);

            return convertToResponse(entity);
        }
        return new TestRunResponse(runId, "NOT_FOUND", "Test run not found");
    }

    public List<TestRunResponse> getAllTestRuns() {
        return testRunRepository.findAllByOrderByStartTimeDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<TestRunResponse> getTestRunsByStatus(String status) {
        return testRunRepository.findByStatusOrderByStartTimeDesc(status)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void updateTestRunStatus(String runId, String status, String reportUrl,
                                    Integer totalTests, Integer passedTests, Integer failedTests,
                                    String screenshotPaths, String failureDetails) {

        // 🔽 ADD DEBUG LOGGING HERE:
        logger.info("DEBUG TestRunService - Updating run: {}", runId);
        logger.info("DEBUG TestRunService - Screenshot paths received: {}", screenshotPaths);
        logger.info("DEBUG TestRunService - Failure details received: {}", failureDetails);

        Optional<TestRunEntity> entityOpt = testRunRepository.findById(runId);
        if (entityOpt.isPresent()) {
            TestRunEntity entity = entityOpt.get();
            entity.setStatus(status);
            entity.setEndTime(LocalDateTime.now());
            entity.setReportUrl(reportUrl);
            entity.setTotalTests(totalTests);
            entity.setPassedTests(passedTests);
            entity.setFailedTests(failedTests);

            // 👇 STORE SCREENSHOT PATHS AND FAILURE DETAILS
            entity.setScreenshotPaths(screenshotPaths);
            entity.setFailureDetails(failureDetails);

            // 🔽 ADD DEBUG BEFORE SAVE:
            logger.info("DEBUG TestRunService - Before save - ScreenshotPaths: {}", entity.getScreenshotPaths());
            logger.info("DEBUG TestRunService - Before save - FailureDetails: {}", entity.getFailureDetails());

            testRunRepository.save(entity);

            // 🔽 ADD DEBUG AFTER SAVE:
            logger.info("DEBUG TestRunService - After save - Successfully updated database");
        } else {
            logger.error("DEBUG TestRunService - Run not found: {}", runId);
        }
    }

    private TestRunResponse convertToResponse(TestRunEntity entity) {
        TestRunResponse response = new TestRunResponse();
        response.setRunId(entity.getRunId());
        response.setStatus(entity.getStatus());
        response.setStartTime(entity.getStartTime());
        response.setEndTime(entity.getEndTime());
        response.setReportUrl(entity.getReportUrl());
        response.setTotalTests(entity.getTotalTests());
        response.setPassedTests(entity.getPassedTests());
        response.setFailedTests(entity.getFailedTests());
        response.setMessage("Status: " + entity.getStatus());
        return response;
    }
}