package store.myproject.onlineshop.global.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import store.myproject.onlineshop.domain.MessageCode;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtil {

    private final MessageSource messageSource;

    public String get(MessageCode code) {
        return messageSource.getMessage(code.key(), null, Locale.getDefault());
    }

    public String get(MessageCode code, Object... args) {
        return messageSource.getMessage(code.key(), args, Locale.getDefault());
    }
}
