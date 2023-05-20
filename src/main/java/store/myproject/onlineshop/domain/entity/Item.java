package store.myproject.onlineshop.domain.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
