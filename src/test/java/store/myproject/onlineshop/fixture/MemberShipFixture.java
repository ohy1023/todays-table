package store.myproject.onlineshop.fixture;

import net.datafaker.Faker;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.dto.membership.MemberShipCreateRequest;
import store.myproject.onlineshop.dto.membership.MemberShipDto;
import store.myproject.onlineshop.dto.membership.MemberShipUpdateRequest;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public class MemberShipFixture {
    private static final Faker faker = new Faker(Locale.KOREA);

    public static MemberShip createBronzeMembership() {
        return MemberShip.builder()
                .uuid(UUID.fromString(faker.internet().uuid()))
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();
    }
    public static MemberShip createSilverMembership() {
        return MemberShip.builder()
                .uuid(UUID.fromString(faker.internet().uuid()))
                .baseline(new BigDecimal(100_000))
                .discountRate(BigDecimal.valueOf(0.1))
                .level(Level.SILVER)
                .build();
    }
    public static MemberShip createGoldMembership() {
        return MemberShip.builder()
                .uuid(UUID.fromString(faker.internet().uuid()))
                .baseline(new BigDecimal(200_000))
                .discountRate(BigDecimal.valueOf(0.2))
                .level(Level.GOLD)
                .build();
    }

    public static MemberShipDto createBronzeDto() {
        return MemberShipDto.builder()
                .uuid(UUID.fromString(faker.internet().uuid()))
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();
    }

    public static MemberShipDto createSilverDto() {
        return MemberShipDto.builder()
                .uuid(UUID.fromString(faker.internet().uuid()))
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
