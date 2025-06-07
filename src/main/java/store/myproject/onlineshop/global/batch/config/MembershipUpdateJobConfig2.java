package store.myproject.onlineshop.global.batch.config;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.dto.CustomerMembershipUpdateDto;
import store.myproject.onlineshop.global.batch.CustomerMembershipProcessor;
import store.myproject.onlineshop.global.batch.CustomerMembershipWriter;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.membership.MemberShipRepository;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MembershipUpdateJobConfig2 {

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
                .<Customer, CustomerMembershipUpdateDto>chunk(1000, transactionManager)
                .reader(customerItemReader(entityManagerFactory))
                .processor(customerMembershipProcessor())
                .writer(customerItemWriter())
                .build();
    }


    @Bean
    public JpaPagingItemReader<Customer> customerItemReader(EntityManagerFactory entityManagerFactory) {
        JpaPagingItemReader<Customer> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setQueryString("SELECT c FROM Customer c");
        reader.setPageSize(1000);
        return reader;
    }

    @Bean
    public CustomerMembershipProcessor customerMembershipProcessor() {
        List<MemberShip> memberships = memberShipRepository.findAll();
        return new CustomerMembershipProcessor(memberships);
    }

    @Bean
    public CustomerMembershipWriter customerItemWriter() {
        return new CustomerMembershipWriter(customerRepository);
    }
}
