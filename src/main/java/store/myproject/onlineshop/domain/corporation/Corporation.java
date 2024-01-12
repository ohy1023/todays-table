package store.myproject.onlineshop.domain.corporation;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.customer.CustomerRole;

import java.math.BigDecimal;

import static store.myproject.onlineshop.domain.customer.CustomerRole.*;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Corporation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String companyTel;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(unique = true)
    private String companyEmail;

    private String password;

    @Enumerated(EnumType.STRING)
    private CustomerRole customerRole;


    @PrePersist
    public void prePersist() {
        this.customerRole = this.customerRole == null ? ROLE_CORPORATION : this.customerRole;
    }
}
