package store.myproject.onlineshop.repository.order;

import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

import java.util.List;

public interface OrderCustomRepository {

    List<Order> findMyOrders(OrderSearchCond cond, Customer customer);
}
