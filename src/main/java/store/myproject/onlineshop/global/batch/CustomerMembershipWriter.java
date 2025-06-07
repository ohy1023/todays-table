package store.myproject.onlineshop.global.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import store.myproject.onlineshop.domain.order.dto.CustomerMembershipUpdateDto;
import store.myproject.onlineshop.repository.customer.CustomerRepository;


@RequiredArgsConstructor
public class CustomerMembershipWriter implements ItemWriter<CustomerMembershipUpdateDto> {

    private final CustomerRepository customerRepository;

    @Override
    public void write(Chunk<? extends CustomerMembershipUpdateDto> items) {
        for (CustomerMembershipUpdateDto item : items) {
            customerRepository.updateMembershipId(item.getCustomerId(), item.getMembershipId());
        }
    }

}