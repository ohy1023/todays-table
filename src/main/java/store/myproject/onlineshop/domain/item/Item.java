package store.myproject.onlineshop.domain.item;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE item SET deleted_date = CURRENT_TIMESTAMP WHERE item_id = ?")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    private String itemName;

    private int price;

    private int stock;

    private String itemPhotoUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();

    public void updateItem(ItemUpdateRequest updateRequest, Brand findBrand) {
        this.itemName = updateRequest.getItemName();
        this.price = updateRequest.getPrice();
        this.stock = updateRequest.getStock();
        this.brand = findBrand;
        this.itemPhotoUrl = updateRequest.getItemPhotoUrl();
    }


    public ItemDto toItemDto() {
        return ItemDto.builder()
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock)
                .itemPhotoUrl(this.itemPhotoUrl)
                .brand(this.brand)
                .build();
    }

}
