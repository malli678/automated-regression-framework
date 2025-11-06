package com.regression.framework.worker.listener;

import com.regression.framework.models.TestExecutionJob;
import com.regression.framework.worker.service.TestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WorkerListener {

    private static final Logger logger = LoggerFactory.getLogger(WorkerListener.class);

    @Autowired
    private TestExecutor testExecutor;

    @RabbitListener(queues = "test-execution-queue")
    public void receiveTestExecutionJob(TestExecutionJob job) {
        logger.info("Received test execution job: {}", job.getJobId());

        try {
            testExecutor.executeTests(job);
            logger.info("Successfully executed job: {}", job.getJobId());
        } catch (Exception e) {
            logger.error("Failed to execute job: {}", job.getJobId(), e);
            // Handle retry logic here
            handleFailedJob(job, e);
        }
    }

    private void handleFailedJob(TestExecutionJob job, Exception e) {
        if (job.getRetryCount() < job.getTestRunRequest().getRetryCount()) {
            job.setRetryCount(job.getRetryCount() + 1);
            logger.info("Retrying job: {} (attempt {})", job.getJobId(), job.getRetryCount());
            // In a real implementation, you'd re-queue the job
        } else {
            logger.error("Job {} failed after {} retries", job.getJobId(), job.getRetryCount());
            job.setStatus("FAILED");
            // Send failure results
            testExecutor.sendTestResults(job, null);
        }
    }
}