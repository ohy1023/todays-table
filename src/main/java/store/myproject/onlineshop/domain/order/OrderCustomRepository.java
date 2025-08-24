package store.myproject.onlineshop.domain.order;

import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.dto.order.MyOrderFlatDto;
import store.myproject.onlineshop.dto.order.OrderSearchCond;

import java.util.List;

public interface OrderCustomRepository {

    List<Long> findMyOrderIds(OrderSearchCond cond, Customer customer);

    List<MyOrderFlatDto> findMyOrders(List<Long> orderIds);
}
