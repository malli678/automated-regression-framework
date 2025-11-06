package com.regression.framework.worker.service;

import com.regression.framework.models.TestExecutionJob;
import com.regression.framework.models.TestRunRequest;
import com.regression.framework.models.TestResult;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
//import org.apache.commons.io.FileUtils;
import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TestExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TestExecutor.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ReportGenerator reportGenerator;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void executeTests(TestExecutionJob job) {
        logger.info("Starting test execution for run: {}", job.getRunId());

        // Update job status to RUNNING
        job.setStatus("RUNNING");

        List<TestResult> testResults = new ArrayList<>();

        try {
            TestRunRequest request = job.getTestRunRequest();

            // Execute based on type
            switch (request.getExecutionType()) {
                case WEB:
                    testResults.addAll(executeWebTests(job));
                    break;
                case API:
                    testResults.addAll(executeApiTests(job));
                    break;
                case ALL:
                    List<CompletableFuture<List<TestResult>>> futures = new ArrayList<>();

                    futures.add(CompletableFuture.supplyAsync(() -> executeWebTests(job), executorService));
                    futures.add(CompletableFuture.supplyAsync(() -> executeApiTests(job), executorService));

                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                    for (CompletableFuture<List<TestResult>> future : futures) {
                        testResults.addAll(future.get());
                    }
                    break;
            }

            job.setStatus("COMPLETED");
            job.setTestResults(testResults);
            logger.info("Test execution completed for run: {}. Total tests: {}", job.getRunId(), testResults.size());

        } catch (Exception e) {
            job.setStatus("FAILED");
            logger.error("Test execution failed for run: {}", job.getRunId(), e);
        }

        // Generate report and send results
        String reportUrl = reportGenerator.generateReport(job);
        sendTestResults(job, reportUrl);
    }

    private List<TestResult> executeWebTests(TestExecutionJob job) {
        logger.info("Executing web tests for job: {}", job.getJobId());

        List<TestResult> results = new ArrayList<>();
        WebDriver driver = null;

        try {
            // Setup Chrome driver
            System.setProperty("webdriver.chrome.driver", getChromeDriverPath());
            ChromeOptions options = new ChromeOptions();
            //options.addArguments("--headless");  // ← Commented out to see browser
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--disable-gpu");
            options.addArguments("--window-size=1920,1080");

            driver = new ChromeDriver(options);

            // Example test 1 - navigate to Google
            TestResult test1 = new TestResult("GooglePageTest", "FAILED"); // 👈 START AS FAILED
            long startTime = System.currentTimeMillis();

            try {
                driver.get("https://www.google.com");
                String title = driver.getTitle();
                logger.info("Page title: {}", title);

                // 🔽 ADD DEBUG LOGGING:
                String lowerTitle = title.toLowerCase();
                logger.info("DEBUG - Lowercase title: {}", lowerTitle);
                logger.info("DEBUG - Contains 'google': {}", lowerTitle.contains("google"));

                if (title.toLowerCase().contains("google")) {
                    test1.setStatus("PASSED");
                    logger.info("DEBUG - Google test PASSED");
                } else {
                    test1.setStatus("FAILED");
                    logger.info("DEBUG - Google test FAILED");
                    test1.setErrorMessage("Expected title to contain 'google', but was: " + title);
                    String screenshotPath = captureScreenshot(driver, "GoogleTest_Failure");
                    test1.setScreenshotPath(screenshotPath);
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                test1.setStatus("FAILED");
                test1.setErrorMessage(e.getMessage());
                test1.setStackTrace(getStackTrace(e));
                String screenshotPath = captureScreenshot(driver, "GoogleTest_Exception");
                test1.setScreenshotPath(screenshotPath);
            }

            test1.setExecutionTime(System.currentTimeMillis() - startTime);
            test1.setEndTime(java.time.LocalDateTime.now());
            results.add(test1);

            // Example test 2 - navigate to GitHub
            TestResult test2 = new TestResult("GitHubPageTest", "FAILED"); // 👈 START AS FAILED
            startTime = System.currentTimeMillis();

            try {
                driver.get("https://github.com");
                String title = driver.getTitle();
                logger.info("Page title: {}", title);

                String lowerTitle = title.toLowerCase();
                logger.info("DEBUG - Lowercase title: {}", lowerTitle);
                logger.info("DEBUG - Contains 'github': {}", lowerTitle.contains("github"));

                if (title.toLowerCase().contains("github")) {
                    test2.setStatus("PASSED");
                    logger.info("DEBUG - GitHub test PASSED");
                } else {
                    test2.setStatus("FAILED");
                    logger.info("DEBUG - GitHub test FAILED");
                    test2.setErrorMessage("Expected title to contain 'github', but was: " + title);
                    String screenshotPath = captureScreenshot(driver, "GitHubTest_Failure");
                    test2.setScreenshotPath(screenshotPath);
                }

                Thread.sleep(1000);
            } catch (Exception e) {
                test2.setStatus("FAILED");
                test2.setErrorMessage(e.getMessage());
                test2.setStackTrace(getStackTrace(e));
                String screenshotPath = captureScreenshot(driver, "GitHubTest_Exception");
                test2.setScreenshotPath(screenshotPath);
            }

            test2.setExecutionTime(System.currentTimeMillis() - startTime);
            test2.setEndTime(java.time.LocalDateTime.now());
            results.add(test2);

        } catch (Exception e) {
            logger.error("Web test execution failed", e);
            TestResult errorResult = new TestResult("WebTestSuite", "FAILED");
            errorResult.setErrorMessage("Web test suite failed: " + e.getMessage());
            errorResult.setStackTrace(getStackTrace(e));
            results.add(errorResult);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return results;
    }

    // 🔽 ADD THIS NEW METHOD AT THE END OF THE CLASS
    private String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Create screenshots directory
            java.nio.file.Path screenshotsDir = java.nio.file.Paths.get("screenshots");
            if (!java.nio.file.Files.exists(screenshotsDir)) {
                java.nio.file.Files.createDirectories(screenshotsDir);
                logger.info("Created screenshots directory");
            }

            // Capture screenshot - this uses File class
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String screenshotPath = "screenshots/" + screenshotName + "_" +
                    System.currentTimeMillis() + ".png";

            // Save to file using Java NIO
            java.nio.file.Files.copy(
                    screenshot.toPath(),
                    java.nio.file.Paths.get(screenshotPath),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );

            logger.info("Screenshot captured: {}", screenshotPath);
            return screenshotPath;
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }

    private List<TestResult> executeApiTests(TestExecutionJob job) {
        logger.info("Executing API tests for job: {}", job.getJobId());

        List<TestResult> results = new ArrayList<>();

        try {
            // Example REST-Assured test 1
            TestResult test1 = new TestResult("JSONPlaceholderAPI Test", "PASSED");
            long startTime = System.currentTimeMillis();

            try {
                // This would be replaced with actual REST-Assured code
                // given().baseUri("https://jsonplaceholder.typicode.com")
                // .when().get("/posts/1")
                // .then().statusCode(200);

                // Simulate API test execution
                Thread.sleep(500);
                logger.info("API test 1 executed successfully");

            } catch (Exception e) {
                test1.setStatus("FAILED");
                test1.setErrorMessage(e.getMessage());
                test1.setStackTrace(getStackTrace(e));
            }

            test1.setExecutionTime(System.currentTimeMillis() - startTime);
            test1.setEndTime(java.time.LocalDateTime.now());
            results.add(test1);

            // Example test 2
            TestResult test2 = new TestResult("PublicAPI Test", "PASSED");
            startTime = System.currentTimeMillis();

            try {
                // Simulate another API test
                Thread.sleep(300);
                logger.info("API test 2 executed successfully");

            } catch (Exception e) {
                test2.setStatus("FAILED");
                test2.setErrorMessage(e.getMessage());
                test2.setStackTrace(getStackTrace(e));
            }

            test2.setExecutionTime(System.currentTimeMillis() - startTime);
            test2.setEndTime(java.time.LocalDateTime.now());
            results.add(test2);

        } catch (Exception e) {
            logger.error("API test execution failed", e);
            TestResult errorResult = new TestResult("APITestSuite", "FAILED");
            errorResult.setErrorMessage("API test suite failed: " + e.getMessage());
            errorResult.setStackTrace(getStackTrace(e));
            results.add(errorResult);
        }

        return results;
    }

    public void sendTestResults(TestExecutionJob job, String reportUrl) {
        try {
            rabbitTemplate.convertAndSend("test-results-queue", job);
            logger.info("Sent test results for job: {}", job.getJobId());
        } catch (Exception e) {
            logger.error("Failed to send test results", e);
        }
    }

    private String getChromeDriverPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "chromedriver.exe";
        } else {
            return "chromedriver";
        }
    }

    private String getStackTrace(Exception e) {
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}