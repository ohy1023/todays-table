package store.myproject.onlineshop.domain.membership;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.dto.membership.MemberShipUpdateRequest;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE member_ship SET deleted_date = CURRENT_TIMESTAMP WHERE member_ship_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(name = "member_ship")
public class MemberShip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_ship_id")
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "member_ship_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private Level level;

    @Column(name = "discount_rate")
    private BigDecimal discountRate;

    @Column(name = "baseline")
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

}
