package com.regression.framework.api.listener;

import com.regression.framework.models.TestExecutionJob;
import com.regression.framework.api.service.TestRunService;
import com.regression.framework.models.TestResult; // 🔽 ADDED THIS IMPORT
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TestResultsListener {

    private static final Logger logger = LoggerFactory.getLogger(TestResultsListener.class);

    @Autowired
    private TestRunService testRunService;

    @RabbitListener(queues = "test-results-queue")
    public void receiveTestResults(TestExecutionJob job) {
        logger.info("Received test results for run: {}", job.getRunId());

        try {
            // Calculate test statistics
            int totalTests = 0;
            int passedTests = 0;
            int failedTests = 0;

            // 👇 COLLECT SCREENSHOT PATHS AND FAILURE DETAILS
            StringBuilder screenshotPaths = new StringBuilder();
            StringBuilder failureDetails = new StringBuilder();

            if (job.getTestResults() != null) {
                totalTests = job.getTestResults().size();

                // 🔽 REPLACE THE STREAM CODE WITH THIS LOOP:
                for (TestResult result : job.getTestResults()) {
                    if ("PASSED".equals(result.getStatus())) {
                        passedTests++;
                    } else if ("FAILED".equals(result.getStatus())) {
                        failedTests++;

                        // 👇 COLLECT SCREENSHOT PATHS FOR FAILED TESTS
                        if (result.getScreenshotPath() != null) {
                            screenshotPaths.append(result.getScreenshotPath()).append(",");
                        }

                        // 👇 COLLECT FAILURE DETAILS
                        failureDetails.append(result.getTestName())
                                .append(": ")
                                .append(result.getErrorMessage())
                                .append("; ");
                    }
                }
                // 🔼 END OF REPLACEMENT
            }

            String reportUrl = "/reports/" + job.getRunId() + ".html";
            // 🔽 ADD DEBUG LOGGING HERE:
            logger.info("DEBUG - Total tests: {}", totalTests);
            logger.info("DEBUG - Failed tests: {}", failedTests);
            logger.info("DEBUG - Screenshot paths collected: {}", screenshotPaths.toString());
            logger.info("DEBUG - Failure details collected: {}", failureDetails.toString());

            // 🔽 ADD THESE NEW DEBUG LINES:
            logger.info("DEBUG - Calling TestRunService.updateTestRunStatus");
            logger.info("DEBUG - Run ID: {}", job.getRunId());
            logger.info("DEBUG - Screenshot paths to save: {}", screenshotPaths.toString());

            testRunService.updateTestRunStatus(job.getRunId(), job.getStatus(),
                    reportUrl, totalTests, passedTests, failedTests,
                    screenshotPaths.toString(), failureDetails.toString());

            logger.info("DEBUG - After calling TestRunService.updateTestRunStatus");

            logger.info("Updated test run status for: {}", job.getRunId());
        } catch (Exception e) {
            logger.error("Failed to process test results for run: {}", job.getRunId(), e);
        }
    }
}