package store.myproject.onlineshop.global.batch;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.dto.CustomerMembershipUpdateDto;

import java.util.List;

@RequiredArgsConstructor
public class CustomerMembershipProcessor implements ItemProcessor<Customer, CustomerMembershipUpdateDto> {

    private final List<MemberShip> memberships;

    @Override
    public CustomerMembershipUpdateDto process(@NotNull Customer customer) {
        for (MemberShip membership : memberships) {
            if (customer.getMonthlyPurchaseAmount().compareTo(membership.getBaseline()) >= 0) {
                return new CustomerMembershipUpdateDto(customer.getId(), membership.getId());
            }
        }
        return null; // 업데이트 필요 없는 경우
    }
}
