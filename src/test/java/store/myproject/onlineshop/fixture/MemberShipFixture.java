package store.myproject.onlineshop.fixture;

import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;

public class MemberShipFixture {

    public static MemberShip createBronzeMembership() {
        return MemberShip.builder()
                .id(1L)
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();
    }

    public static MemberShip createSilverMembership() {
        return MemberShip.builder()
                .id(2L)
                .baseline(new BigDecimal("100000"))
                .discountRate(new BigDecimal("0.1"))
                .level(Level.SILVER)
                .build();
    }

    public static MemberShip createGoldMembership() {
        return MemberShip.builder()
                .id(3L)
                .baseline(new BigDecimal("1000000"))
                .discountRate(new BigDecimal("0.15"))
                .level(Level.GOLD)
                .build();
    }

    public static MemberShip createDiamondMembership() {
        return MemberShip.builder()
                .id(4L)
                .baseline(new BigDecimal("10000000"))
                .discountRate(new BigDecimal("0.3"))
                .level(Level.DIAMOND)
                .build();
    }
}
