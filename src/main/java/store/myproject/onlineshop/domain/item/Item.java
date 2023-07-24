package store.myproject.onlineshop.domain.item;


import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

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

    @Column(name = "item_name")
    private String itemName;

    private Long price;

    private Long stock;

    @Column(name = "item_photo_url")
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

    public void decrease(Long count) {
        if (stock < count) {
            throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
        }

        this.stock -= count;
    }

    public void increase(Long count) {
        this.stock += count;
    }


    public void updateItem(ItemUpdateRequest updateRequest, Brand findBrand) {
        this.itemName = updateRequest.getItemName();
        this.price = updateRequest.getPrice();
        this.brand = findBrand;
        this.itemPhotoUrl = updateRequest.getItemPhotoUrl();
        this.stock = updateRequest.getStock();
    }


    public ItemDto toItemDto() {
        return ItemDto.builder()
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock)
                .itemPhotoUrl(this.itemPhotoUrl)
                .brandName(this.brand.getName())
                .build();
    }

}
