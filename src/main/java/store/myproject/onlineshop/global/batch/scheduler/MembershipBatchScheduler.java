package store.myproject.onlineshop.global.batch.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MembershipBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job membershipUpdateJob;

//    @Scheduled(cron = "0 0 19 * * ?", zone = "Asia/Seoul") // 매일 19:00 실행
    @Scheduled(cron = "0 30 23 L * ?", zone = "Asia/Seoul") // 매월 말일 23:30 실행
    public void runJob() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(membershipUpdateJob, params);
    }
}
