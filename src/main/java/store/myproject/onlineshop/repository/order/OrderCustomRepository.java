package store.myproject.onlineshop.repository.order;

import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.dto.MyOrderFlatDto;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

import java.util.List;

public interface OrderCustomRepository {

    List<Long> findMyOrderIds(OrderSearchCond cond, Customer customer);

    List<MyOrderFlatDto> findMyOrders(List<Long> orderIds);
}
