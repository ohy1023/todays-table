package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    private String bankName;

    private String accountNumber;

    private String depositor;
}