package store.myproject.onlineshop.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.service.MemberShipService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class MemberShipInitializer implements CommandLineRunner {

    private final MemberShipService memberShipService;

    @Override
    public void run(String... args) {
        createIfNotExists(Level.BRONZE, BigDecimal.ZERO, BigDecimal.ZERO);
        createIfNotExists(Level.SILVER, BigDecimal.valueOf(100_000), BigDecimal.valueOf(0.1));
        createIfNotExists(Level.GOLD, BigDecimal.valueOf(1_000_000), BigDecimal.valueOf(0.15));
        createIfNotExists(Level.DIAMOND, BigDecimal.valueOf(10_000_000), BigDecimal.valueOf(0.3));
    }

    private void createIfNotExists(Level level, BigDecimal baseline, BigDecimal discountRate) {
        if (!memberShipService.existsByLevel(level)) {
            MemberShipCreateRequest request = MemberShipCreateRequest.builder()
                    .baseline(baseline)
                    .level(level)
                    .discountRate(discountRate)
                    .build();
            memberShipService.createMemberShip(request);
        }
    }
}
