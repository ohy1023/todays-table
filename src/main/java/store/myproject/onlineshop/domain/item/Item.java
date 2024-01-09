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
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
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
// 논리적 삭제를 위한 WHERE 절을 정의합니다.
@Where(clause = "deleted_date IS NULL")
// 사용자 정의 SQL 문을 통한 논리적 삭제를 정의합니다.
@SQLDelete(sql = "UPDATE item SET deleted_date = CURRENT_TIMESTAMP WHERE item_id = ?")
public class Item extends BaseEntity {
    // Item 엔티티의 기본 키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    // 상품의 이름
    @Column(name = "item_name")
    private String itemName;

    // 상품의 가격
    private Long price;

    // 상품의 재고 수량
    private Long stock;

    // 상품 사진의 URL
    @Column(name = "item_photo_url")
    private String itemPhotoUrl;

    // Brand 엔티티와의 다대일 관계
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // OrderItem 엔티티들과의 일대다 관계
    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<RecipeItem> recipeItemList = new ArrayList<>();

    // 상품에 브랜드를 추가하는 메서드
    public void addBrand(Brand brand) {
        this.brand = brand;
    }

    // 상품의 재고를 감소시키는 메서드
    public void decrease(Long count) {
        if (stock < count) {
            // 재고가 부족하면 예외를 던집니다.
            throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
        }

        this.stock -= count;
    }

    // 상품의 재고를 증가시키는 메서드
    public void increase(Long count) {
        this.stock += count;
    }

    // 상품을 업데이트하는 메서드 (요청과 브랜드를 기반으로)
    public void updateItem(ItemUpdateRequest updateRequest, Brand findBrand) {
        this.itemName = updateRequest.getItemName();
        this.price = updateRequest.getPrice();
        this.brand = findBrand;
        this.itemPhotoUrl = updateRequest.getItemPhotoUrl();
        this.stock = updateRequest.getStock();
    }

    // 상품을 DTO로 변환하는 메서드
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
