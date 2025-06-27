package store.myproject.onlineshop.global.batch.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.customer.*;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.membership.MemberShipRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * application-test.yml이 정상적으로 설정(레디스 정보, S3 정보 등) 되어 있어야 성공
 */
@SpringBootTest
@SpringBatchTest
@Import(MembershipUpdateJobConfig.class)
@ActiveProfiles("test")
class MembershipUpdateJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private MemberShipRepository memberShipRepository;


    @BeforeEach
    void setUp() {
        List<MemberShip> memberships = createMemberships();
        List<MemberShip> savedMemberShips = memberShipRepository.saveAll(memberships);

        for (int i = 1; i <= 3000; i++) {
            Customer customer = Customer.builder()
                    .email("user" + i + "@example.com")
                    .password("testPassword123!")
                    .userName("홍길동")
                    .nickName("hong123")
                    .gender(Gender.MALE)
                    .tel("010-1234-5678")
                    .address(Address.builder()
                            .city("서울")
                            .street("테스트로")
                            .detail("123")
                            .zipcode("04524")
                            .build())
                    .customerRole(CustomerRole.ROLE_USER)
                    .monthlyPurchaseAmount(BigDecimal.valueOf(500000))
                    .memberShip(savedMemberShips.get(0))
                    .build();
            customerRepository.save(customer);
        }
    }


    @Test
    @DisplayName("스프링 배치 실행 성공")
    public void spring_batch_success() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis()) // JobInstance 구분용
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);

    }

    @Test
    @DisplayName("jpaPagingItemReader 실행 중 고객 탈퇴")
    public void jpa_paging_item_reader_deleted_customer() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        new Thread(() -> {
            try {
                Thread.sleep(500);
                customerRepository.findById(1500L).ifPresent(c -> {
                    c.setDeletedDate(LocalDateTime.now());
                    customerRepository.save(c);
                });
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        long totalCount = customerRepository.countByMemberShip(memberShipRepository.findById(1L).get());

        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(totalCount).isEqualTo(0);
    }

    public static List<MemberShip> createMemberships() {
        return List.of(
                MemberShip.builder()
                        .uuid(UUID.randomUUID())
                        .level(Level.BRONZE)
                        .baseline(BigDecimal.ZERO)
                        .discountRate(BigDecimal.valueOf(0.01))
                        .build(),
                MemberShip.builder()
                        .uuid(UUID.randomUUID())
                        .level(Level.SILVER)
                        .baseline(BigDecimal.valueOf(100000))
                        .discountRate(BigDecimal.valueOf(0.03))
                        .build(),
                MemberShip.builder()
                        .uuid(UUID.randomUUID())
                        .level(Level.GOLD)
                        .baseline(BigDecimal.valueOf(300000))
                        .discountRate(BigDecimal.valueOf(0.05))
                        .build(),
                MemberShip.builder()
                        .uuid(UUID.randomUUID())
                        .level(Level.DIAMOND)
                        .baseline(BigDecimal.valueOf(500000))
                        .discountRate(BigDecimal.valueOf(0.07))
                        .build()
        );
    }
}