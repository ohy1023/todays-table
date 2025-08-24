package store.myproject.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMembershipUpdateDto {

    private Long customerId;

    private Long membershipId;

}
