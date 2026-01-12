package store.myproject.onlineshop.global.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.Job;

import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MembershipBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job membershipUpdateJob;

    @Scheduled(cron = "0 0 2 1 * ?", zone = "Asia/Seoul") // 매월 1일 2시 실행
    public void runJob() throws Exception {
        String runDate = LocalDate.now().toString();
        JobParameters params = new JobParametersBuilder()
                .addString("runDate", runDate)
                .toJobParameters();
        jobLauncher.run(membershipUpdateJob, params);
    }
}