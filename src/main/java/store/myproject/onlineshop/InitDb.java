//package store.myproject.onlineshop;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import store.myproject.onlineshop.domain.customer.Level;
//import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
//import store.myproject.onlineshop.service.MemberShipService;
//
//@Component
//@RequiredArgsConstructor
//public class InitDb {
//    private final InitService initService;
//
//    @PostConstruct
//    public void init() {
//        initService.dbInit();
//    }
//
//
//    @Component
//    @Transactional
//    @RequiredArgsConstructor
//    static class InitService {
//        private final MemberShipService memberShipService;
//
//        public void dbInit() {
//            MemberShipCreateRequest bronze = MemberShipCreateRequest.builder()
//                    .baseline(0L)
//                    .level(Level.BRONZE)
//                    .discountRate(0F)
//                    .build();
//
//            memberShipService.saveMemberShip(bronze);
//
//            MemberShipCreateRequest silver = MemberShipCreateRequest.builder()
//                    .baseline(100000L)
//                    .level(Level.SILVER)
//                    .discountRate(0.1F)
//                    .build();
//
//            memberShipService.saveMemberShip(silver);
//
//            MemberShipCreateRequest gold = MemberShipCreateRequest.builder()
//                    .baseline(1000000L)
//                    .level(Level.GOLD)
//                    .discountRate(0.3F)
//                    .build();
//
//            memberShipService.saveMemberShip(gold);
//
//            MemberShipCreateRequest diamond = MemberShipCreateRequest.builder()
//                    .baseline(10000000L)
//                    .level(Level.DIAMOND)
//                    .discountRate(0.5F)
//                    .build();
//
//            memberShipService.saveMemberShip(diamond);
//        }
//    }
//
//
//}