package store.myproject.onlineshop.global.batch.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;

import org.springframework.batch.infrastructure.item.database.JpaPagingItemReader;
import org.springframework.batch.infrastructure.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.dto.order.CustomerMembershipUpdateDto;
import store.myproject.onlineshop.global.batch.CustomerMembershipProcessor;
import store.myproject.onlineshop.global.batch.CustomerMembershipWriter;
import store.myproject.onlineshop.domain.customer.CustomerRepository;
import store.myproject.onlineshop.domain.membership.MemberShipRepository;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MembershipUpdateJobConfig {

    private final CustomerRepository customerRepository;
    private final MemberShipRepository memberShipRepository;

    @Bean
    public Job membershipUpdateJob(JobRepository jobRepository, Step membershipUpdateStep) {
        return new JobBuilder("membershipUpdateJob", jobRepository)
                .start(membershipUpdateStep)
                .build();
    }

    @Bean
    public Step membershipUpdateStep(JobRepository jobRepository,
                                     PlatformTransactionManager transactionManager,
                                     EntityManagerFactory entityManagerFactory
    ) {
        return new StepBuilder("membershipUpdateStep", jobRepository)
                .<Customer, CustomerMembershipUpdateDto>chunk(1000)
                .reader(customerItemReader(entityManagerFactory))
                .processor(customerMembershipProcessor())
                .writer(customerItemWriter())
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public JpaPagingItemReader<Customer> customerItemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<Customer>()
                .name("customerItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT c FROM Customer c")
                .pageSize(1000)
                .build();
    }

    @Bean
    @StepScope
    public CustomerMembershipProcessor customerMembershipProcessor() {
        List<MemberShip> memberships = memberShipRepository.findAll(Sort.by(Sort.Direction.DESC, "baseline"));
        return new CustomerMembershipProcessor(memberships);
    }

    @Bean
    public CustomerMembershipWriter customerItemWriter() {
        return new CustomerMembershipWriter(customerRepository);
    }
}