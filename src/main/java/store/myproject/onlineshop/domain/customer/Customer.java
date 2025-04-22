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
import static store.myproject.onlineshop.domain.customer.CustomerRole.*;

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

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "user_name")
    private String userName;

    @Setter
    private String password;

    private String tel;

    @Column(name = "total_purchase_amount")
    private BigDecimal totalPurchaseAmount;

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

    @PrePersist
    public void prePersist() {
        this.customerRole = this.customerRole == null ? ROLE_USER : this.customerRole;
        this.totalPurchaseAmount = this.totalPurchaseAmount == null ? new BigDecimal(0) : this.totalPurchaseAmount;
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

    public void setTempPassword(String tempPassword) {
        this.password = tempPassword;
    }

    public void setAdmin() {
        this.customerRole = ROLE_ADMIN;
    }

    public void addPurchaseAmount(BigDecimal price) {
        this.totalPurchaseAmount = this.totalPurchaseAmount.add(price);
    }

    public void upgradeMemberShip(MemberShip memberShip) {
        this.memberShip = memberShip;
    }

    public CustomerTempPasswordResponse toCustomerTempPasswordResponse(String tempPassword) {
        return CustomerTempPasswordResponse
                .builder()
                .email(this.email)
                .tempPassword(tempPassword)
                .build();
    }


}
