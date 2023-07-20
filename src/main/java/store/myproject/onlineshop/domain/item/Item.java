package store.myproject.onlineshop.domain.item;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.domain.stock.Stock;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Slf4j
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

    private Long price;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private String itemPhotoUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();


    public void addBrand(Brand brand) {
        this.brand = brand;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
        stock.setItem(this);
    }

    public void updateItem(ItemUpdateRequest updateRequest, Brand findBrand) {
        this.itemName = updateRequest.getItemName();
        this.price = updateRequest.getPrice();
        this.brand = findBrand;
        this.itemPhotoUrl = updateRequest.getItemPhotoUrl();
        this.stock.updateQuantity(updateRequest.getStock());
    }


    public ItemDto toItemDto() {
        return ItemDto.builder()
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock.getQuantity())
                .itemPhotoUrl(this.itemPhotoUrl)
                .brandName(this.brand.getName())
                .build();
    }

}
