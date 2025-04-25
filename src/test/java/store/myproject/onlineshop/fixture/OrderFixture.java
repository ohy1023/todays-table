package store.myproject.onlineshop.fixture;


import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
import store.myproject.onlineshop.domain.order.dto.PostVerificationRequest;
import store.myproject.onlineshop.domain.order.dto.PreparationRequest;

import java.math.BigDecimal;

public class OrderFixture {

    private static final Faker faker = new Faker();

    public static OrderInfoRequest createOrderInfoRequest() {
        return OrderInfoRequest.builder()
                .itemId(faker.number().randomNumber())
                .itemCnt(faker.number().numberBetween(1L, 5L))
                .recipientName(faker.name().fullName())
                .recipientTel(faker.phoneNumber().cellPhone())
                .recipientCity(faker.address().city())
                .recipientStreet(faker.address().streetAddress())
                .recipientDetail(faker.address().secondaryAddress())
                .recipientZipcode(faker.address().zipCode())
                .merchantUid(faker.commerce().promotionCode())
                .totalPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
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
                .recipientTel(faker.phoneNumber().cellPhone())
                .recipientAddress(faker.address().fullAddress())
                .zipcode(faker.address().zipCode())
                .deliveryStatus("COMPLETED")
                .totalPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
                .build();
    }

    public static PreparationRequest createPreparationRequest() {
        return new PreparationRequest("merchant-" + faker.number().digits(5), new BigDecimal(faker.number().randomNumber()));
    }

    public static PostVerificationRequest createPostVerificationRequest() {
        return new PostVerificationRequest("imp_" + faker.number().digits(6), "merchant_" + faker.number().digits(6));
    }
} 
