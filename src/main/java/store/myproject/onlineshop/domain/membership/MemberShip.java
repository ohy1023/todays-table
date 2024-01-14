package store.myproject.onlineshop.domain.membership;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE member_ship SET deleted_date = CURRENT_TIMESTAMP WHERE member_ship_id = ?")
public class MemberShip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ship_id")
    private Long id;

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
                .level(this.level)
                .baseline(this.baseline)
                .discountRate(this.discountRate)
                .build();
    }


}
