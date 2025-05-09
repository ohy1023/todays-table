package store.myproject.onlineshop.fixture;


import com.github.javafaker.Faker;

import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.order.dto.*;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

public class OrderFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static OrderInfoRequest createOrderInfoRequest() {
        return OrderInfoRequest.builder()
                .itemUuid(UUID.fromString(faker.internet().uuid()))
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
        return OrderInfo.builder()
                .brandName(faker.company().name())
                .itemName(faker.commerce().productName())
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

    public static CancelItemRequest createCancelItemRequest(UUID uuid) {
        return new CancelItemRequest(uuid);
    }

    public static PreparationRequest createPreparationRequest() {
        return new PreparationRequest(UUID.randomUUID().toString(), new BigDecimal(faker.number().randomNumber()));
    }

    public static PostVerificationRequest createPostVerificationRequest() {
        return new PostVerificationRequest(UUID.randomUUID(), "imp_" + faker.number().digits(6));
    }
}
