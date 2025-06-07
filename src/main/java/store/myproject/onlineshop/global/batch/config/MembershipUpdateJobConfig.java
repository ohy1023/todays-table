//package store.myproject.onlineshop.global.batch.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//import store.myproject.onlineshop.service.OrderService;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class MembershipUpdateJobConfig {
//
//    private final OrderService orderService;
//
//    @Bean
//    public Job membershipUpdateJob(
//            JobRepository jobRepository,
//            PlatformTransactionManager transactionManager
//    ) {
//        return new JobBuilder("membershipUpdateJob", jobRepository)
//                .start(updateMembershipStep(jobRepository, transactionManager))
//                .build();
//    }
//
//    @Bean
//    public Step updateMembershipStep(
//            JobRepository jobRepository,
//            PlatformTransactionManager transactionManager
//    ) {
//        return new StepBuilder("updateMembershipStep", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    log.info("[배치] 회원 등급 갱신 작업 시작");
//
//                    orderService.updateMonthlyPurchaseAmounts();
//
//                    log.info("[배치] 회원 등급 갱신 작업 완료");
//                    return RepeatStatus.FINISHED;
//                }, transactionManager)
//                .build();
//    }
//}
