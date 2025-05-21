package store.myproject.onlineshop.repository.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.global.config.TestConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Nested
    @DisplayName("이메일로 고객 조회")
    class FindByEmail {

        @Test
        @DisplayName("성공")
        void find_by_email_success() {
            // given
            Customer customer = CustomerFixture.createCustomer();
            customerRepository.save(customer);

            // when
            Optional<Customer> result = customerRepository.findByEmail(customer.getEmail());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo(customer.getEmail());
        }

        @Test
        @DisplayName("실패 - 존재하지 않음")
        void find_by_email_fail() {
            // when
            Optional<Customer> result = customerRepository.findByEmail("notfound@example.com");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("이메일과 전화번호로 고객 조회")
    class FindByEmailAndTel {

        @Test
        @DisplayName("성공")
        void find_by_email_and_tel_success() {
            // given
            Customer customer = CustomerFixture.createCustomer();
            customerRepository.save(customer);

            // when
            Optional<Customer> result = customerRepository.findByEmailAndTel(customer.getEmail(), customer.getTel());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo(customer.getEmail());
            assertThat(result.get().getTel()).isEqualTo(customer.getTel());
        }

        @Test
        @DisplayName("실패 - 존재하지 않음")
        void find_by_email_and_tel_fail() {
            // when
            Optional<Customer> result = customerRepository.findByEmailAndTel("notfound@example.com", "000-0000-0000");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("닉네임으로 고객 조회 ")
    class FindByNickName {

        @Test
        @DisplayName("성공")
        void find_by_nickname_success() {
            // given
            Customer customer = CustomerFixture.createCustomer();
            customerRepository.save(customer);

            // when
            Optional<Customer> result = customerRepository.findByNickName(customer.getNickName());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getNickName()).isEqualTo(customer.getNickName());
        }

        @Test
        @DisplayName("실패 - 존재하지 않음")
        void find_by_nickname_fail() {
            // when
            Optional<Customer> result = customerRepository.findByNickName("ghost");

            // then
            assertThat(result).isEmpty();
        }
    }
}
