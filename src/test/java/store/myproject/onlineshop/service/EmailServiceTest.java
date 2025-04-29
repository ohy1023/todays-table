package store.myproject.onlineshop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @Test
    @DisplayName("임시 비밀번호 이메일 전송 성공")
    void send_email_for_temp_password_success() throws MessagingException {
        // given
        String email = "test@example.com";
        String tempPassword = "tempPassword123";

        given(mailSender.createMimeMessage())
                .willReturn(mimeMessage);

        // when
        emailService.sendEmailForTempPassword(email, tempPassword);

        // then
        then(mailSender).should(times(1)).createMimeMessage();
        then(mailSender).should(times(1)).send(mimeMessage);
    }
}
