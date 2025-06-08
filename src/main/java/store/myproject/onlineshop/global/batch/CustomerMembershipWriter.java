package store.myproject.onlineshop.global.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import store.myproject.onlineshop.domain.order.dto.CustomerMembershipUpdateDto;
import store.myproject.onlineshop.repository.customer.CustomerRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomerMembershipWriter implements ItemWriter<CustomerMembershipUpdateDto> {

    private final CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends CustomerMembershipUpdateDto> items) {
        // membershipId별로 customerId를 그룹핑
        Map<Long, List<Long>> membershipIdToCustomerIds = items.getItems().stream()
                .collect(Collectors.groupingBy(
                        CustomerMembershipUpdateDto::getMembershipId,
                        Collectors.mapping(CustomerMembershipUpdateDto::getCustomerId, Collectors.toList())
                ));

        // 각 그룹별로 벌크 업데이트 실행
        for (Map.Entry<Long, List<Long>> entry : membershipIdToCustomerIds.entrySet()) {
            Long membershipId = entry.getKey();
            List<Long> customerIds = entry.getValue();
            customerRepository.updateMemberships(customerIds, membershipId);
        }
    }
}
