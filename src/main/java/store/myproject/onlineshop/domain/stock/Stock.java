package store.myproject.onlineshop.domain.stock;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.exception.AppException;

import static store.myproject.onlineshop.exception.ErrorCode.NOT_ENOUGH_STOCK;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    private Long quantity;

    @OneToOne(mappedBy = "stock")
    private Item item;


    public void decrease(Long quantity) {
        if (this.quantity < quantity) {
            throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
        }
        this.quantity -= quantity;
    }

    public void updateQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void increase(Long quantity) {
        this.quantity += quantity;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
