package store.myproject.onlineshop.domain.customer;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.account.Account;
import store.myproject.onlineshop.domain.account.dto.*;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static store.myproject.onlineshop.domain.customer.CustomerRole.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

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
    private Account account;

    private BigDecimal totalPurchaseAmount;

    @Embedded
    private Address address;

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

    @PrePersist
    public void prePersist() {
        this.customerRole = this.customerRole == null ? ROLE_CUSTOMER : this.customerRole;
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

    public void registerAccount(AccountCreateRequest request) {
        this.account = request.toEntity();
    }

    public void updateAccount(AccountUpdateRequest request) {
        this.account = request.toEntity();
    }

    public void deleteAccount() {
        this.account = Account
                .builder()
                .bankName(null)
                .accountNumber(null)
                .depositor(null)
                .build();
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
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

    public void purchase(BigDecimal price) {
        this.account.minusMyAssets(price);
    }

    public void upgradeMemberShip(MemberShip memberShip) {
        if (this.totalPurchaseAmount.compareTo(memberShip.getBaseline()) >= 0) {
            this.memberShip = memberShip;
        } else {
            throw new AppException(NOT_ENOUGH_MEMBERSHIP, NOT_ENOUGH_MEMBERSHIP.getMessage());
        }
    }

    public AccountCreateResponse toAccountCreateResponse() {
        return AccountCreateResponse.builder()
                .bankName(this.account.getBankName())
                .accountNumber(this.account.getAccountNumber())
                .depositor(this.account.getDepositor())
                .build();
    }

    public AccountUpdateResponse toAccountUpdateResponse() {
        return AccountUpdateResponse.builder()
                .bankName(this.account.getBankName())
                .accountNumber(this.account.getAccountNumber())
                .depositor(this.account.getDepositor())
                .build();
    }

    public AccountDeleteResponse toAccountDeleteResponse() {
        return AccountDeleteResponse.builder()
                .bankName(this.account.getBankName())
                .accountNumber(this.account.getAccountNumber())
                .depositor(this.account.getDepositor())
                .build();
    }

    public AccountInfo toAccountInfo() {
        return AccountInfo.builder()
                .bankName(this.account.getBankName())
                .accountNumber(this.account.getAccountNumber())
                .depositor(this.account.getDepositor())
                .build();
    }

    public CustomerTempPasswordResponse toCustomerTempPasswordResponse(String tempPassword) {
        return CustomerTempPasswordResponse
                .builder()
                .email(this.email)
                .tempPassword(tempPassword)
                .build();
    }


}
