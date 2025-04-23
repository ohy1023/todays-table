package store.myproject.onlineshop.global.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import store.myproject.onlineshop.domain.MessageCode;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    MessageUtil messageUtil;

    @Test
    @DisplayName("단순 메시지 코드 반환")
    void get_message_code() {
        // given
        MessageCode code = MessageCode.CUSTOMER_JOIN;
        given(messageSource.getMessage(eq(code.key()), isNull(), any(Locale.class)))
                .willReturn("성공입니다");

        // when
        String result = messageUtil.get(code);

        // then
        then(messageSource).should().getMessage(eq(code.key()), isNull(), any(Locale.class));
        assertThat(result).isEqualTo("성공입니다");
    }

    @Test
    @DisplayName("파라미터가 있는 메시지 코드 반환")
    void get_message_code_with_args() {
        // given
        MessageCode code = MessageCode.CUSTOMER_JOIN;
        Object[] args = {"상품"};
        given(messageSource.getMessage(eq(code.key()), eq(args), any(Locale.class)))
                .willReturn("상품을 찾을 수 없습니다");

        // when
        String result = messageUtil.get(code, args);

        // then
        then(messageSource).should().getMessage(eq(code.key()), eq(args), any(Locale.class));
        assertThat(result).isEqualTo("상품을 찾을 수 없습니다");
    }
}
