package store.myproject.onlineshop.domain.membership.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "회원 등급 정보 DTO")
public class MemberShipDto {

    @Schema(description = "회원 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID uuid;

    @Schema(description = "회원 등급", example = "SILVER")
    private Level level;

    @Schema(description = "기준 금액", example = "100000")
    private BigDecimal baseline;

    @Schema(description = "할인율", example = "0.10")
    private BigDecimal discountRate;


    public static MemberShipDto from(final MemberShip memberShip) {

        return MemberShipDto.builder()
                .uuid(memberShip.getUuid())
                .level(memberShip.getLevel())
                .baseline(memberShip.getBaseline())
                .discountRate(memberShip.getDiscountRate())
                .build();
    }
}
