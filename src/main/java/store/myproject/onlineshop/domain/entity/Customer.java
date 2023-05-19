package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.enums.Gender;
import store.myproject.onlineshop.domain.enums.Role;

import static jakarta.persistence.FetchType.*;
import static store.myproject.onlineshop.domain.enums.Role.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE customer SET deleted_date = CURRENT_TIMESTAMP WHERE customer_id = ?")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "customer_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String userName;

    private String password;

    private String tel;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, columnDefinition = "varchar(10) default 'CUSTOMER'")
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_ship_id")
    private MemberShip memberShip;

    @PrePersist
    public void prePersist() {
        this.role = this.role == null ? CUSTOMER : this.role;
    }

    @Builder
    public Customer(Long id, String email, String userName, String password, String tel, Address address, Role role, Gender gender, MemberShip memberShip) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.tel = tel;
        this.address = address;
        this.role = role;
        this.gender = gender;
        this.memberShip = memberShip;
    }
}
