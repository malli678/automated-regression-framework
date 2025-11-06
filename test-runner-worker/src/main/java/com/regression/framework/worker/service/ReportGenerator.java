package com.regression.framework.worker.service;

import com.regression.framework.models.TestExecutionJob;
import com.regression.framework.models.TestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    private static final String REPORT_DIR = "reports";

    public String generateReport(TestExecutionJob job) {
        createReportDirectory();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportFileName = String.format("test_report_%s_%s.html", job.getRunId(), timestamp);
        String reportPath = REPORT_DIR + File.separator + reportFileName;

        try (FileWriter writer = new FileWriter(reportPath)) {
            String htmlReport = generateHtmlReport(job);
            writer.write(htmlReport);
            logger.info("Generated HTML report: {}", reportPath);

            // Also generate CSV and JUnit reports
            generateCsvReport(job, timestamp);
            generateJUnitReport(job, timestamp);

        } catch (IOException e) {
            logger.error("Failed to generate HTML report", e);
            return "Error generating report";
        }

        return reportPath;
    }

    private String generateHtmlReport(TestExecutionJob job) {
        List<TestResult> testResults = job.getTestResults();
        int totalTests = testResults != null ? testResults.size() : 0;
        long passedTests = testResults != null ? testResults.stream().filter(r -> "PASSED".equals(r.getStatus())).count() : 0;
        long failedTests = testResults != null ? testResults.stream().filter(r -> "FAILED".equals(r.getStatus())).count() : 0;
        double passPercentage = totalTests > 0 ? (passedTests * 100.0) / totalTests : 0;

        StringBuilder testResultsHtml = new StringBuilder();
        if (testResults != null) {
            for (TestResult result : testResults) {
                String statusColor = "PASSED".equals(result.getStatus()) ? "green" : "red";
                testResultsHtml.append(String.format("""
                    <tr>
                        <td>%s</td>
                        <td style="color: %s; font-weight: bold;">%s</td>
                        <td>%s</td>
                        <td>%d ms</td>
                        <td>%s</td>
                    </tr>
                    """,
                        result.getTestName(),
                        statusColor,
                        result.getStatus(),
                        result.getStartTime(),
                        result.getExecutionTime(),
                        result.getErrorMessage() != null ? result.getErrorMessage() : ""
                ));
            }
        }

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Test Execution Report</title>
                <style>
                    body { 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
                        margin: 0; 
                        padding: 20px; 
                        background-color: #f5f5f5; 
                    }
                    .container { 
                        max-width: 1200px; 
                        margin: 0 auto; 
                        background: white; 
                        padding: 30px; 
                        border-radius: 10px; 
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1); 
                    }
                    .header { 
                        background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); 
                        color: white; 
                        padding: 30px; 
                        border-radius: 8px; 
                        margin-bottom: 30px; 
                        text-align: center; 
                    }
                    .summary-cards { 
                        display: grid; 
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); 
                        gap: 20px; 
                        margin-bottom: 30px; 
                    }
                    .card { 
                        background: white; 
                        padding: 20px; 
                        border-radius: 8px; 
                        box-shadow: 0 2px 5px rgba(0,0,0,0.1); 
                        text-align: center; 
                        border-left: 4px solid #667eea; 
                    }
                    .card.total { border-left-color: #3498db; }
                    .card.passed { border-left-color: #2ecc71; }
                    .card.failed { border-left-color: #e74c3c; }
                    .card.percentage { border-left-color: #f39c12; }
                    .card h3 { 
                        margin: 0 0 10px 0; 
                        font-size: 14px; 
                        color: #666; 
                        text-transform: uppercase; 
                    }
                    .card .value { 
                        font-size: 24px; 
                        font-weight: bold; 
                        margin: 0; 
                    }
                    .test-info { 
                        background: #f8f9fa; 
                        padding: 20px; 
                        border-radius: 8px; 
                        margin-bottom: 30px; 
                    }
                    .test-info h2 { 
                        margin-top: 0; 
                        color: #333; 
                    }
                    .results-table { 
                        width: 100%%; 
                        border-collapse: collapse; 
                        margin-top: 20px; 
                    }
                    .results-table th, 
                    .results-table td { 
                        padding: 12px; 
                        text-align: left; 
                        border-bottom: 1px solid #ddd; 
                    }
                    .results-table th { 
                        background-color: #f8f9fa; 
                        font-weight: 600; 
                        color: #333; 
                    }
                    .results-table tr:hover { 
                        background-color: #f5f5f5; 
                    }
                    .timestamp { 
                        text-align: center; 
                        color: #666; 
                        margin-top: 30px; 
                        font-size: 12px; 
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚀 Test Execution Report</h1>
                        <p>Automated Regression Test Suite Framework</p>
                    </div>
                    
                    <div class="summary-cards">
                        <div class="card total">
                            <h3>Total Tests</h3>
                            <p class="value">%d</p>
                        </div>
                        <div class="card passed">
                            <h3>Passed</h3>
                            <p class="value">%d</p>
                        </div>
                        <div class="card failed">
                            <h3>Failed</h3>
                            <p class="value">%d</p>
                        </div>
                        <div class="card percentage">
                            <h3>Success Rate</h3>
                            <p class="value">%.1f%%</p>
                        </div>
                    </div>
                    
                    <div class="test-info">
                        <h2>📋 Test Run Information</h2>
                        <p><strong>Run ID:</strong> %s</p>
                        <p><strong>Job ID:</strong> %s</p>
                        <p><strong>Status:</strong> <span style="color: %s; font-weight: bold;">%s</span></p>
                        <p><strong>Test Suite:</strong> %s</p>
                        <p><strong>Execution Type:</strong> %s</p>
                        <p><strong>Browser:</strong> %s</p>
                        <p><strong>Retry Count:</strong> %d</p>
                    </div>
                    
                    <h2>📊 Detailed Test Results</h2>
                    <table class="results-table">
                        <thead>
                            <tr>
                                <th>Test Name</th>
                                <th>Status</th>
                                <th>Start Time</th>
                                <th>Duration</th>
                                <th>Error Message</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>
                    
                    <div class="timestamp">
                        Report generated on: %s
                    </div>
                </div>
            </body>
            </html>
            """,
                totalTests, passedTests, failedTests, passPercentage,
                job.getRunId(),
                job.getJobId(),
                "COMPLETED".equals(job.getStatus()) ? "green" : "red",
                job.getStatus(),
                job.getTestRunRequest().getTestSuiteName(),
                job.getTestRunRequest().getExecutionType(),
                job.getTestRunRequest().getBrowserType(),
                job.getRetryCount(),
                testResultsHtml.toString(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private void generateCsvReport(TestExecutionJob job, String timestamp) {
        String csvFileName = String.format("test_report_%s_%s.csv", job.getRunId(), timestamp);
        String csvPath = REPORT_DIR + File.separator + csvFileName;

        try (FileWriter writer = new FileWriter(csvPath)) {
            writer.write("TestName,Status,StartTime,EndTime,ExecutionTime(ms),ErrorMessage\\n");

            if (job.getTestResults() != null) {
                for (TestResult result : job.getTestResults()) {
                    writer.write(String.format("%s,%s,%s,%s,%d,%s\\n",
                            result.getTestName(),
                            result.getStatus(),
                            result.getStartTime(),
                            result.getEndTime(),
                            result.getExecutionTime(),
                            result.getErrorMessage() != null ? result.getErrorMessage().replace(",", ";") : ""
                    ));
                }
            }
            logger.info("Generated CSV report: {}", csvPath);
        } catch (IOException e) {
            logger.error("Failed to generate CSV report", e);
        }
    }

    private void generateJUnitReport(TestExecutionJob job, String timestamp) {
        String junitFileName = String.format("TEST-%s-%s.xml", job.getRunId(), timestamp);
        String junitPath = REPORT_DIR + File.separator + junitFileName;

        try (FileWriter writer = new FileWriter(junitPath)) {
            int totalTests = job.getTestResults() != null ? job.getTestResults().size() : 0;
            long failedTests = job.getTestResults() != null ?
                    job.getTestResults().stream().filter(r -> "FAILED".equals(r.getStatus())).count() : 0;

            writer.write(String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n"));
            writer.write(String.format("<testsuite name=\"%s\" tests=\"%d\" failures=\"%d\" time=\"0\">\\n",
                    job.getTestRunRequest().getTestSuiteName(), totalTests, failedTests));

            if (job.getTestResults() != null) {
                for (TestResult result : job.getTestResults()) {
                    writer.write(String.format("  <testcase name=\"%s\" time=\"%.3f\">\\n",
                            result.getTestName(), result.getExecutionTime() / 1000.0));

                    if ("FAILED".equals(result.getStatus())) {
                        writer.write(String.format("    <failure message=\"%s\">%s</failure>\\n",
                                result.getErrorMessage() != null ? escapeXml(result.getErrorMessage()) : "Test failed",
                                result.getStackTrace() != null ? escapeXml(result.getStackTrace()) : ""));
                    }

                    writer.write("  </testcase>\\n");
                }
            }

            writer.write("</testsuite>\\n");
            logger.info("Generated JUnit report: {}", junitPath);
        } catch (IOException e) {
            logger.error("Failed to generate JUnit report", e);
        }
    }

    private String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private void createReportDirectory() {
        File directory = new File(REPORT_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}