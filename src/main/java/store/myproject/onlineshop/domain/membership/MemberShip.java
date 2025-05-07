package store.myproject.onlineshop.domain.membership;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberShip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ship_id")
    private Long id;

    @Column(name = "member_ship_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    private BigDecimal baseline;

    public void updateMemberShip(MemberShipUpdateRequest updateRequest) {
        this.level = updateRequest.getLevel();
        this.baseline = updateRequest.getBaseline();
        this.discountRate = updateRequest.getDiscountRate();
    }


    public BigDecimal applyDiscount(BigDecimal itemPrice) {
        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(discountRate);
        return itemPrice.multiply(discountMultiplier);
    }

    public MemberShipDto toDto() {
        return MemberShipDto.builder()
                .uuid(this.uuid)
                .level(this.level)
                .baseline(this.baseline)
                .discountRate(this.discountRate)
                .build();
    }


}
