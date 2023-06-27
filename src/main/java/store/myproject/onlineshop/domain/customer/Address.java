package store.myproject.onlineshop.domain.customer;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    private String city;

    private String street;

    private String detail;

    private String zipcode;

    @Builder
    public Address(String city, String street, String detail, String zipcode) {
        this.city = city;
        this.street = street;
        this.detail = detail;
        this.zipcode = zipcode;
    }
}