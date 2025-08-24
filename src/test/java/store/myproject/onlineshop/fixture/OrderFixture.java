package store.myproject.onlineshop.fixture;


import com.github.javafaker.Faker;

import store.myproject.onlineshop.dto.cart.CartOrderRequest;
import store.myproject.onlineshop.dto.order.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class OrderFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static OrderInfoRequest createOrderInfoRequest() {
        return OrderInfoRequest.builder()
                .itemUuid(UUID.fromString(faker.internet().uuid()))
                .merchantUid(UUID.fromString(faker.internet().uuid()))
                .itemCnt(faker.number().numberBetween(1L, 5L))
                .recipientName(faker.name().fullName())
                .recipientTel("010-" + faker.number().digits(4) + "-" + faker.number().digits(4))
                .recipientCity(faker.address().city())
                .recipientStreet(faker.address().streetAddress())
                .recipientDetail(faker.address().secondaryAddress())
                .recipientZipcode(faker.address().zipCode())
                .build();
    }

    public static OrderInfo createOrderInfo() {
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        orderItemResponses.add(OrderItemResponse.builder()
                .itemUuid(UUID.fromString(faker.internet().uuid()))
                .orderPrice(BigDecimal.valueOf(1000L))
                .count(2L)
                .brandUuid(UUID.randomUUID())
                .brandName(faker.company().name())
                .itemUuid(UUID.randomUUID())
                .itemName(faker.commerce().productName())
                .build());

        return OrderInfo.builder()
                .orderItemList(orderItemResponses)
                .orderDate(faker.date().birthday().toString())
                .orderStatus("DELIVERED")
                .orderCustomerName(faker.name().fullName())
                .orderCustomerTel(faker.phoneNumber().cellPhone())
                .recipientName(faker.name().fullName())
                .recipientTel("010-" + faker.number().digits(4) + "-" + faker.number().digits(4))
                .recipientAddress(faker.address().fullAddress())
                .zipcode(faker.address().zipCode())
                .deliveryStatus("COMPLETED")
                .totalPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
                .build();
    }

    public static CartOrderRequest createCartOrderRequest() {
        return CartOrderRequest
                .builder()
                .recipientName(faker.name().fullName())
                .recipientTel("010-" + faker.number().digits(4) + "-" + faker.number().digits(4))
                .recipientCity(faker.address().city())
                .recipientStreet(faker.address().streetAddress())
                .recipientDetail(faker.address().secondaryAddress())
                .recipientZipcode(faker.address().zipCode())
                .build();
    }

    public static CancelItemRequest createCancelItemRequest(String impUid, UUID uuid) {
        return new CancelItemRequest(impUid, List.of(uuid));
    }

    public static PreparationRequest createPreparationRequest() {
        return new PreparationRequest(new BigDecimal(faker.number().randomNumber()));
    }

    public static PostVerificationRequest createPostVerificationRequest() {
        return new PostVerificationRequest(UUID.randomUUID(), "imp_" + faker.number().digits(6));
    }
}
