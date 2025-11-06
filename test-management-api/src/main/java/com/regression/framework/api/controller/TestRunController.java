package com.regression.framework.api.controller;

import com.regression.framework.models.TestRunRequest;
import com.regression.framework.models.TestRunResponse;
import com.regression.framework.api.service.TestRunService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/runs")
@CrossOrigin(origins = "*")
public class TestRunController {

    @Autowired
    private TestRunService testRunService;

    @PostMapping
    public ResponseEntity<TestRunResponse> scheduleTestRun(
            @Valid @RequestBody TestRunRequest testRunRequest) {

        TestRunResponse response = testRunService.scheduleTestRun(testRunRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{runId}")
    public ResponseEntity<TestRunResponse> getTestRunStatus(@PathVariable String runId) {
        TestRunResponse response = testRunService.getTestRunStatus(runId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{runId}/stop")
    public ResponseEntity<TestRunResponse> stopTestRun(@PathVariable String runId) {
        TestRunResponse response = testRunService.stopTestRun(runId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TestRunResponse>> getAllTestRuns() {
        List<TestRunResponse> responses = testRunService.getAllTestRuns();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TestRunResponse>> getTestRunsByStatus(@PathVariable String status) {
        List<TestRunResponse> responses = testRunService.getTestRunsByStatus(status);
        return ResponseEntity.ok(responses);
    }
}