package store.myproject.onlineshop.domain.cart;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.item.Item;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @Column(name = "cart_item_cnt")
    private Long cartItemCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    public void plusItemCnt(Long cnt) {
        this.cartItemCnt += cnt;
    }

    public static CartItem createCartItem(Item findItem, Long itemCnt, Cart cart) {
        return CartItem.builder()
                .item(findItem)
                .cartItemCnt(itemCnt)
                .cart(cart)
                .build();
    }
}