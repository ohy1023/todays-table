package store.myproject.onlineshop.domain.customer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE Customer SET deleted_date = CURRENT_TIMESTAMP WHERE customer_id = ?")
@Table(
        uniqueConstraints = {
                // email과 deleted_date 컬럼을 묶어서 복합 유니크 제약 조건 추가
                @UniqueConstraint(
                        name = "uq_customer_email_deleted_date", // 제약 조건 이름
                        columnNames = {"email", "deleted_date"}
                )
        }
)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;

    private String email;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "user_name")
    private String userName;

    @Setter
    private String password;

    private String tel;

    @Column(name = "monthly_purchase_amount")
    private BigDecimal monthlyPurchaseAmount;

    @Embedded
    private Address address;

    @Column(name = "customer_role")
    @Enumerated(EnumType.STRING)
    private CustomerRole customerRole;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_ship_id")
    private MemberShip memberShip;

    @Builder.Default
    @OneToMany(mappedBy = "customer")
    private List<Order> orderList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "customer")
    private List<Like> likeList = new ArrayList<>();


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

    public void setTempPassword(String tempPassword) {
        this.password = tempPassword;
    }

}
