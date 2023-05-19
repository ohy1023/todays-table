package store.myproject.onlineshop.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String itemName;

    private int price;

    private int stock;

    private String itemPhotoUrl;

    @Builder
    public Item(Long id, String itemName, int price, int stock, String itemPhotoUrl) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.stock = stock;
        this.itemPhotoUrl = itemPhotoUrl;
    }
}
