package store.myproject.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job membershipUpdateJob;

    @GetMapping("/test/batch")
    public ResponseEntity<String> runMembershipUpdateJob() {
        try {
            String runDate = String.valueOf(System.currentTimeMillis());
            JobParameters params = new JobParametersBuilder()
                    .addString("runDate", runDate)
                    .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(membershipUpdateJob, params);

            return ResponseEntity.ok("Batch job started with status: " + jobExecution.getStatus());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            return ResponseEntity.badRequest().body("Batch job failed to start: " + e.getMessage());
        }
    }
}
