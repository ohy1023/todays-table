//package store.myproject.onlineshop.service;
//
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.context.TestConstructor;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
//import store.myproject.onlineshop.domain.item.Item;
//import store.myproject.onlineshop.domain.item.repository.ItemRepository;
//import store.myproject.onlineshop.domain.order.Order;
//import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
//import store.myproject.onlineshop.domain.order.repository.OrderRepository;
//import store.myproject.onlineshop.domain.orderitem.repository.OrderItemRepository;
//
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
//@RequiredArgsConstructor
//class OrderServiceTest {
//
//
//    private final OrderRepository orderRepository;
//
//    private final CustomerRepository customerRepository;
//
//    private final OrderItemRepository orderItemRepository;
//
//    private final OrderService orderService;
//
//    Customer buyer;
//    final OrderInfoRequest orderInfoRequest =
//            OrderInfoRequest.builder()
//                    .itemId(1L)
//                    .itemCnt(1L)
//                    .recipientCity("city")
//                    .recipientDetail("detail")
//                    .recipientName("name")
//                    .recipientStreet("street")
//                    .recipientTel("tel")
//                    .recipientZipcode("zipcode")
//                    .build();
//    @Autowired
//    private ItemRepository itemRepository;
//
//
//    @BeforeEach
//    void init() {
//        buyer = customerRepository.findById(1L).get();
//    }
//
//    @Test
//    @DisplayName("동시에 100명이 구매하려는 상황")
//    public void purchase() throws Exception {
//
//
//        final int PURCHASE_PEOPLE = 10;
//
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//
//        CountDownLatch latch = new CountDownLatch(PURCHASE_PEOPLE);
//
//        for (int i = 0; i < PURCHASE_PEOPLE; i++) {
//            executorService.submit(() -> {
//                try {
//                    orderService.orderByOne(orderInfoRequest, buyer.getEmail());
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        Item item = itemRepository.findById(1L).orElseThrow();
//
//        System.out.println(item.getStock());
//
//    }
//
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
//            orderService.orderByOne(orderInfoRequest, buyer.getEmail());
//            countDownLatch.countDown();
//        }
//
//    }
//}