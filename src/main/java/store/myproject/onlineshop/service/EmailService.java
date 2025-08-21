package store.myproject.onlineshop.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async(value = "mailExecutor")
    public void sendEmailForTempPassword(String email, String tempPassword) throws MessagingException {
        String content = String.format("임시 비밀번호 : %s", tempPassword);
        sendMail("임시 비밀번호 안내", email, content);
    }

    private void sendMail(String title, String email, String content) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email);
        helper.setSubject(title);
        helper.setText(content);
        helper.setFrom("ohy971023@naver.com");
        mailSender.send(mimeMessage);
    }

}
