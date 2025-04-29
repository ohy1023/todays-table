package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import store.myproject.onlineshop.domain.alert.Alert;
import store.myproject.onlineshop.domain.alert.AlertType;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.repository.alert.AlertRepository;
import store.myproject.onlineshop.repository.alert.EmitterRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EmitterRepository emitterRepository;

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    @Test
    @DisplayName("알림 구독 성공")
    void subscribe_success() {
        // given
        String email = "test@email.com";
        String lastEventId = "";

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .build();

        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(emitterRepository.save(anyString(), any(SseEmitter.class)))
                .willAnswer(invocation -> invocation.getArgument(1));

        // when
        SseEmitter emitter = alertService.subscribe(email, lastEventId);

        // then
        assertThat(emitter).isNotNull();
        then(customerRepository).should(times(1)).findByEmail(email);
        then(emitterRepository).should(times(1)).save(anyString(), any(SseEmitter.class));
    }

    @Test
    @DisplayName("알림 구독 실패 - 존재하지 않는 이메일")
    void subscribe_fail_customer_not_found() {
        // given
        String email = "notfound@email.com";
        String lastEventId = "";

        given(customerRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> alertService.subscribe(email, lastEventId))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CUSTOMER_NOT_FOUND.getMessage());

        then(customerRepository).should(times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("알림 전송 성공")
    void send_alert_success() {
        // given
        Customer receiver = Customer.builder()
                .id(1L)
                .email("receiver@email.com")
                .build();

        Alert alert = Alert.builder()
                .receiver(receiver)
                .alertType(AlertType.ORDER_COMPLETE)
                .content("주문이 완료되었습니다.")
                .relatedUrl("/orders/123")
                .isRead(false)
                .build();

        Map<String, SseEmitter> emitters = Collections.singletonMap(
                "1_123456789", new SseEmitter()
        );

        given(alertRepository.save(any(Alert.class))).willReturn(alert);
        given(emitterRepository.findAllEmitterStartWithByCustomerId(String.valueOf(receiver.getId())))
                .willReturn(emitters);

        // when
        alertService.send(receiver, AlertType.ORDER_COMPLETE, "주문이 완료되었습니다.", "/orders/123");

        // then
        then(alertRepository).should(times(1)).save(any(Alert.class));
        then(emitterRepository).should(times(1)).findAllEmitterStartWithByCustomerId(anyString());
    }

    @Test
    @DisplayName("알림 전송 실패 - SseEmitter send 중 IOException 발생")
    void send_alert_fail_by_send_exception() throws IOException {
        // given
        Customer receiver = Customer.builder()
                .id(1L)
                .email("receiver@email.com")
                .build();

        Alert alert = Alert.builder()
                .receiver(receiver)
                .alertType(AlertType.ORDER_COMPLETE)
                .content("주문이 완료되었습니다.")
                .relatedUrl("/orders/123")
                .isRead(false)
                .build();

        SseEmitter emitter = mock(SseEmitter.class);

        Map<String, SseEmitter> emitters = Collections.singletonMap(
                "1_123456789", emitter
        );

        given(alertRepository.save(any(Alert.class))).willReturn(alert);
        given(emitterRepository.findAllEmitterStartWithByCustomerId(String.valueOf(receiver.getId())))
                .willReturn(emitters);

        // SseEmitter.send() 호출 시 IOException 발생하도록 설정
        willThrow(IOException.class)
                .given(emitter).send(any(SseEmitter.SseEventBuilder.class));

        // when
        alertService.send(receiver, AlertType.ORDER_COMPLETE, "주문이 완료되었습니다.", "/orders/123");

        // then
        then(emitterRepository).should(times(1)).deleteById(anyString());
    }

    @Test
    @DisplayName("구독 성공 - lastEventId 존재 (sendLostData 호출)")
    void subscribe_success_with_lastEventId() {
        // given
        String email = "test@email.com";
        String lastEventId = "someLastEventId";
        Customer customer = CustomerFixture.createCustomer();  // CustomerFixture에 createdDate 세팅해야 함
        given(customerRepository.findByEmail(email))
                .willReturn(Optional.of(customer));
        given(emitterRepository.save(anyString(), any(SseEmitter.class)))
                .willReturn(new SseEmitter(3600000L));
        given(emitterRepository.findAllEventCacheStartWithByCustomerId(anyString()))
                .willReturn(Map.of(
                        "someLaterEventId", "test event data"  // lastEventId보다 큰 EventId
                ));

        // when
        SseEmitter emitter = alertService.subscribe(email, lastEventId);

        // then
        assertThat(emitter).isNotNull();
        then(customerRepository).should().findByEmail(email);
        then(emitterRepository).should().save(anyString(), any(SseEmitter.class));
        then(emitterRepository).should().findAllEventCacheStartWithByCustomerId(anyString());
    }

    @Test
    @DisplayName("구독 성공 - lastEventId 없음 (sendLostData 호출 안함)")
    void subscribe_success_without_lastEventId() {
        // given
        String email = "test@email.com";
        String lastEventId = "";
        Customer customer = CustomerFixture.createCustomer();
        given(customerRepository.findByEmail(email))
                .willReturn(Optional.of(customer));
        given(emitterRepository.save(anyString(), any(SseEmitter.class)))
                .willReturn(new SseEmitter(3600000L));

        // when
        SseEmitter emitter = alertService.subscribe(email, lastEventId);

        // then
        assertThat(emitter).isNotNull();
        then(customerRepository).should().findByEmail(email);
        then(emitterRepository).should().save(anyString(), any(SseEmitter.class));
        // sendLostData 호출 안 됨
    }

    @Test
    @DisplayName("sendLostData - lastEventId보다 작은 이벤트는 전송 안함")
    void sendLostData_skip_old_events() {
        // given
        String email = "test@email.com";
        String lastEventId = "2024_100";
        Customer customer = CustomerFixture.createCustomer();

        given(customerRepository.findByEmail(email))
                .willReturn(Optional.of(customer));
        given(emitterRepository.save(anyString(), any(SseEmitter.class)))
                .willReturn(new SseEmitter(3600000L));
        given(emitterRepository.findAllEventCacheStartWithByCustomerId(anyString()))
                .willReturn(Map.of(
                        "2024_099", "old event",   // <= lastEventId
                        "2024_100", "same event"   // == lastEventId
                        // 전송 대상 아님
                ));

        // when
        SseEmitter emitter = alertService.subscribe(email, lastEventId);

        // then
        assertThat(emitter).isNotNull();
        then(customerRepository).should().findByEmail(email);
        then(emitterRepository).should().save(anyString(), any(SseEmitter.class));
        then(emitterRepository).should().findAllEventCacheStartWithByCustomerId(anyString());
    }

}
