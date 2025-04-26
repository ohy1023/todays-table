package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class ItemFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Item createItem(Brand brand) {

        return Item.builder()
                .itemName(faker.company().name())
                .price(BigDecimal.valueOf(3000))
                .stock(40L)
                .brand(brand)
                .build();
    }

    public static ItemDto createItemDto() {
        return ItemDto.builder()
                .itemName(faker.commerce().productName())
                .stock(faker.number().numberBetween(1L, 1000L))
                .price(BigDecimal.valueOf(faker.number().numberBetween(1000, 100000)))
                .brandName(faker.company().name())
                .imageList(List.of(
                        faker.internet().image(),
                        faker.internet().image(),
                        faker.internet().image()
                ))
                .build();
    }

    public static ItemCreateRequest createRequest() {
        return ItemCreateRequest.builder()
                .brandName(faker.company().name())
                .itemName(faker.commerce().productName())
                .price(BigDecimal.valueOf(faker.number().numberBetween(1000, 100000)))
                .stock(faker.number().numberBetween(1L, 1000L))
                .build();
    }

    public static ItemUpdateRequest updateRequest() {
        return ItemUpdateRequest.builder()
                .brandName(faker.company().name())
                .itemName(faker.commerce().productName())
                .price(BigDecimal.valueOf(faker.number().numberBetween(1000, 100000)))
                .stock(faker.number().numberBetween(1L, 1000L))
                .build();
    }
}
