package store.myproject.onlineshop.fixture;

import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;

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

    public static MemberShipDto createBronzeDto() {
        return MemberShipDto.builder()
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();
    }

    public static MemberShipDto createSilverDto() {
        return MemberShipDto.builder()
                .baseline(new BigDecimal("100000"))
                .discountRate(new BigDecimal("0.1"))
                .level(Level.SILVER)
                .build();
    }

    public static MemberShipCreateRequest createBronzeRequest() {
        return MemberShipCreateRequest.builder()
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();
    }

    public static MemberShipUpdateRequest updateToBronzeRequest() {
        return new MemberShipUpdateRequest(
                Level.BRONZE,
                new BigDecimal("50000"),
                new BigDecimal("0.05")
        );
    }

}
