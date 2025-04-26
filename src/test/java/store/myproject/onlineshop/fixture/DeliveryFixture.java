package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;

import java.util.Locale;

public class DeliveryFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Delivery createDelivery() {
        return Delivery.builder()
                .address(Address.builder()
                        .city(faker.address().city())
                        .street(faker.address().streetName())
                        .detail(faker.address().streetAddress())
                        .zipcode(faker.address().zipCode())
                        .build())
                .recipientName(faker.name().fullName())
                .recipientTel(faker.phoneNumber().cellPhone())
                .status(DeliveryStatus.READY)
                .build();
    }
}
