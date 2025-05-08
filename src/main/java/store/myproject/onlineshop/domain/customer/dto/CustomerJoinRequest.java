package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.customer.Gender;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 요청 DTO")
public class CustomerJoinRequest {

    @Email
    @Schema(description = "사용자 이메일", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "비밀번호", example = "P@ssw0rd123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotBlank
    @Schema(description = "닉네임", example = "콩거", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickName;

    @NotBlank
    @Schema(description = "이름", example = "오형상", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    private String tel;

    @NotNull
    @Schema(description = "성별 (MALE 또는 FEMALE)", example = "MALE", requiredMode = Schema.RequiredMode.REQUIRED)
    private Gender gender;

    @NotBlank
    @Schema(description = "도시", example = "서울특별시", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank
    @Schema(description = "도로명 주소", example = "강남대로 123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다.")
    @Schema(description = "상세 주소", example = "101동 202호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @NotBlank
    @Schema(description = "우편번호", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipcode;

    public Customer toEntity(String encodedPassword, MemberShip memberShip) {
        return Customer.builder()
                .email(this.email)
                .nickName(this.nickName)
                .userName(this.userName)
                .password(encodedPassword)
                .tel(this.tel)
                .gender(this.gender)
                .address(Address.builder()
                        .city(this.city)
                        .street(this.street)
                        .detail(this.detail)
                        .zipcode(this.zipcode)
                        .build()
                )
                .totalPurchaseAmount(BigDecimal.ZERO)
                .memberShip(memberShip)
                .customerRole(CustomerRole.ROLE_USER)
                .build();
    }


}
