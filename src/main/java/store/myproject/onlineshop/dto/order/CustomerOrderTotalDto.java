package store.myproject.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderTotalDto {
    private Long customerId;
    private BigDecimal totalAmount;
}