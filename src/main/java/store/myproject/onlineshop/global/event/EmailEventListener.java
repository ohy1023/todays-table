package store.myproject.onlineshop.global.event;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import store.myproject.onlineshop.dto.cusotmer.CustomerTempPasswordResponse;
import store.myproject.onlineshop.service.EmailService;

@Component
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = CustomerTempPasswordResponse.class)
    public void handle(CustomerTempPasswordResponse event) throws MessagingException {

        emailService.sendEmailForTempPassword(event.getEmail(), event.getTempPassword());
    }

}