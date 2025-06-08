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
        // 아무 기준도 만족하지 못하면 가장 낮은 멤버십
        MemberShip lowestMembership = memberships.get(memberships.size() - 1);
        return new CustomerMembershipUpdateDto(customer.getId(), lowestMembership.getId());
    }
}
