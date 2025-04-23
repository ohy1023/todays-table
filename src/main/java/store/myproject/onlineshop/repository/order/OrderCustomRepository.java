package store.myproject.onlineshop.repository.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

public interface OrderCustomRepository {

    Page<Order> search(OrderSearchCond orderSearchCond, Customer customer, Pageable pageable);
}
