package store.myproject.onlineshop.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderSumDto {

    private Long customerId;
    private BigDecimal totalOrderPrice;


}
