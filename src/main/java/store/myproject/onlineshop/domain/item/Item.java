package store.myproject.onlineshop.domain.item;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;

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
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE Item SET deleted_date = CURRENT_TIMESTAMP WHERE item_id = ?")
@Table(
        indexes = {
                @Index(name = "idx_deleted_date_item_name", columnList = "deleted_date, item_name"),
                @Index(name = "idx_deleted_date_brand_id", columnList = "deleted_date, brand_id")
        }
)
public class Item extends BaseEntity {
    // Item 엔티티의 기본 키
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    // 상품의 이름
    @Column(name = "item_name")
    private String itemName;

    // 상품의 가격
    private BigDecimal price;

    // 상품의 재고 수량
    private Long stock;

    // Brand 엔티티와의 다대일 관계
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    // 상품 사진의 URL
    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<ImageFile> imageFileList = new ArrayList<>();

    // OrderItem 엔티티들과의 일대다 관계
    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<OrderItem> orderItemList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "item")
    private List<RecipeItem> recipeItemList = new ArrayList<>();


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
        this.stock = updateRequest.getStock();
    }

    // 상품을 DTO로 변환하는 메서드
    public ItemDto toItemDto() {
        return ItemDto.builder()
                .uuid(this.uuid)
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock)
                .brandName(this.brand.getName())
                .imageList(this.getImageFileList().stream()
                        .map(ImageFile::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
