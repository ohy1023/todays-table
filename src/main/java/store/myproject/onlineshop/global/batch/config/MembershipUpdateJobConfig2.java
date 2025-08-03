//package store.myproject.onlineshop.global.batch.config;
//
//import jakarta.persistence.EntityManagerFactory;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.transaction.PlatformTransactionManager;
//import store.myproject.onlineshop.domain.customer.dto.CustomerOrderSumDto;
//import store.myproject.onlineshop.domain.membership.MemberShip;
//import store.myproject.onlineshop.domain.order.dto.CustomerMembershipUpdateDto;
//import store.myproject.onlineshop.global.batch.CustomerMembershipProcessor;
//import store.myproject.onlineshop.global.batch.CustomerMembershipWriter;
//import store.myproject.onlineshop.repository.customer.CustomerRepository;
//import store.myproject.onlineshop.repository.membership.MemberShipRepository;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class MembershipUpdateJobConfig2 {
//
//    private final CustomerRepository customerRepository;
//    private final MemberShipRepository memberShipRepository;
//
//    @Bean
//    public Job membershipUpdateJob(JobRepository jobRepository, Step membershipUpdateStep) {
//        return new JobBuilder("membershipUpdateJob", jobRepository)
//                .start(membershipUpdateStep)
//                .build();
//    }
//
//    @Bean
//    public Step membershipUpdateStep(JobRepository jobRepository,
//                                     PlatformTransactionManager transactionManager,
//                                     EntityManagerFactory entityManagerFactory
//    ) {
//        return new StepBuilder("membershipUpdateStep", jobRepository)
//                .<CustomerOrderSumDto, CustomerMembershipUpdateDto>chunk(1000, transactionManager)
//                .reader(customerItemReader(entityManagerFactory, null, null))
//                .processor(customerMembershipProcessor())
//                .writer(customerItemWriter())
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public JpaPagingItemReader<CustomerOrderSumDto> customerItemReader(EntityManagerFactory entityManagerFactory,
//                                                            @Value("#{T(java.time.LocalDate).now().minusMonths(1).withDayOfMonth(1)}") LocalDate startDate,
//                                                            @Value("#{T(java.time.LocalDate).now().withDayOfMonth(T(java.time.LocalDate).now().lengthOfMonth())}") LocalDate endDate) {
//        JpaPagingItemReader<CustomerOrderSumDto> reader = new JpaPagingItemReader<>();
//        reader.setEntityManagerFactory(entityManagerFactory);
//
//        String jpql = "SELECT new store.myproject.onlineshop.domain.customer.dto.CustomerOrderSumDto(c.id, COALESCE(SUM(o.totalPrice), 0)) " +
//                "FROM Customer c LEFT JOIN Order o ON o.customer = c AND o.createdDate BETWEEN :startDate AND :endDate AND o.orderStatus = 'ORDER' " +
//                "GROUP BY c.id";
//
//        reader.setQueryString(jpql);
//        reader.setParameterValues(
//                Map.of("startDate", startDate.atStartOfDay(),
//                        "endDate", endDate.atTime(23,59,59, 999_999_999))
//        );
//        reader.setPageSize(1000);
//        return reader;
//    }
//
//    @Bean
//    @StepScope
//    public CustomerMembershipProcessor customerMembershipProcessor() {
//        List<MemberShip> memberships = memberShipRepository.findAll(Sort.by(Sort.Direction.DESC, "baseline"));
//        return new CustomerMembershipProcessor(memberships);
//    }
//
//    @Bean
//    public CustomerMembershipWriter customerItemWriter() {
//        return new CustomerMembershipWriter(customerRepository);
//    }
//}
