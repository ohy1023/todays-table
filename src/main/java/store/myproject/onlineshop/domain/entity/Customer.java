package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.dto.customer.CustomerModifyRequest;
import store.myproject.onlineshop.domain.enums.Gender;
import store.myproject.onlineshop.domain.enums.Role;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static store.myproject.onlineshop.domain.enums.Role.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE customer SET deleted_date = CURRENT_TIMESTAMP WHERE customer_id = ?")
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String nickName;

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

    @Builder.Default
    @OneToMany(mappedBy = "customer")
    private List<Order> orderList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.role = this.role == null ? CUSTOMER : this.role;
    }

    public void updateInfo(CustomerModifyRequest request) {
        this.userName = request.getUserName();
        this.nickName = request.getNickName();
        this.tel = request.getTel();
        this.address = Address.builder()
                .city(request.getCity())
                .street(request.getStreet())
                .detail(request.getDetail())
                .zipcode(request.getZipcode())
                .build();

    }

}
