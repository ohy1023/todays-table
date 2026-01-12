package store.myproject.onlineshop.domain.item;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.dto.item.ItemDto;
import store.myproject.onlineshop.dto.item.ItemUpdateRequest;
import store.myproject.onlineshop.domain.order.OrderItem;
import store.myproject.onlineshop.domain.recipe.RecipeItem;
import store.myproject.onlineshop.exception.AppException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static jakarta.persistence.FetchType.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE item SET deleted_date = CURRENT_TIMESTAMP WHERE item_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(
        name = "item",
        indexes = {
                @Index(name = "idx_brand_id", columnList = "brand_id")
        }
)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "item_uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "item_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "stock", nullable = false)
    private Long stock;

    @Setter
    @Column(name = "thumbnail")
    private String thumbnail;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFile> imageFileList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<RecipeItem> recipeItemList = new ArrayList<>();

    // 비즈니스 로직: 재고 감소
    public void decrease(Long count) {
        if (this.stock < count) {
            throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
        }
        this.stock -= count;
    }

    // 비즈니스 로직: 재고 증가
    public void increase(Long count) {
        this.stock += count;
    }

    // 상품 업데이트
    public void updateItem(ItemUpdateRequest updateRequest, Brand findBrand) {
        if (updateRequest.getItemName() != null) this.itemName = updateRequest.getItemName();
        if (updateRequest.getPrice() != null) this.itemPrice = updateRequest.getPrice();
        if (updateRequest.getStock() != null) this.stock = updateRequest.getStock();
        if (findBrand != null) this.brand = findBrand;
    }

    // DTO 변환
    public ItemDto toItemDto() {
        return ItemDto.builder()
                .uuid(this.uuid)
                .itemName(this.itemName)
                .price(this.itemPrice)
                .brandName(this.brand != null ? this.brand.getBrandName() : null)
                .imageList(this.imageFileList.stream()
                        .map(ImageFile::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}