//package store.myproject.onlineshop.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestConstructor;
//import store.myproject.onlineshop.domain.brand.Brand;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.fixture.BrandFixture;
//import store.myproject.onlineshop.global.config.TestConfig;
//import store.myproject.onlineshop.repository.customer.CustomerRepository;
//import store.myproject.onlineshop.domain.item.Item;
//import store.myproject.onlineshop.repository.item.ItemRepository;
//import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
//import store.myproject.onlineshop.exception.AppException;
//import store.myproject.onlineshop.fixture.CustomerFixture;
//import store.myproject.onlineshop.fixture.ItemFixture;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.stream.Stream;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static store.myproject.onlineshop.exception.ErrorCode.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@RequiredArgsConstructor
//public class OrderServiceConcurrencyTest {
//
//    private final OrderService orderService;
//    private final ItemRepository itemRepository;
//    private final CustomerRepository customerRepository;
//
//    Customer buyer;
//    final OrderInfoRequest orderInfoRequest
//            = OrderInfoRequest.builder()
//            .itemId(1L)
//            .itemCnt(1L)
//            .recipientName("test")
//            .recipientTel("010-1234-5678")
//            .recipientCity("서울특별시")
//            .recipientStreet("시흥대로 63")
//            .recipientDetail("101동 1003호")
//            .recipientZipcode("08473")
//            .build();
//
//    @BeforeEach
//    void init() {
//
//        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
//        Brand brand = BrandFixture.createBrand();
//        Item item = itemRepository.save(ItemFixture.createItem(brand));
//        buyer = customerRepository.findById(item.getId()).orElseThrow();
//    }
//
//
//    @Test
//    @DisplayName("동시에 10명이 구매하려는 상황")
//    void immediatePurchase() throws InterruptedException {
//
//        Item item = itemRepository.findById(orderInfoRequest.getItemId())
//                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
//
//        final int PURCHASE_PEOPLE = 10;
//
//        CountDownLatch countDownLatch = new CountDownLatch(PURCHASE_PEOPLE);
//
//        List<ImmediateBuyer> buyers = Stream
//                .generate(() -> new ImmediateBuyer(buyer, countDownLatch))
//                .limit(PURCHASE_PEOPLE).toList();
//
//        buyers.forEach(buyer -> new Thread(buyer).start());
//        countDownLatch.await();
//
//        Item item2 = itemRepository.findById(orderInfoRequest.getItemId())
//                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
//
//        assertThat(item2.getStock()).isEqualTo(item.getStock() - PURCHASE_PEOPLE);
//    }
//
//    private class ImmediateBuyer implements Runnable {
//        private Customer buyer;
//        private CountDownLatch countDownLatch;
//
//        public ImmediateBuyer(Customer buyer, CountDownLatch countDownLatch) {
//            this.buyer = buyer;
//            this.countDownLatch = countDownLatch;
//        }
//
//        @Override
//        public void run() {
//            orderService.placeSingleOrder(orderInfoRequest, buyer.getEmail());
//            countDownLatch.countDown();
//        }
//    }
//
//}
