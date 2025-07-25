package store.myproject.onlineshop.repository.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.dto.MyOrderFlatDto;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;
import store.myproject.onlineshop.mapper.OrderMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final OrderMapper orderMapper;


    @Override
    public List<Long> findMyOrderIds(OrderSearchCond cond, Customer customer) {
        int sizePlusOne = cond.getSize() + 1;
        cond.setSizePlusOne(sizePlusOne);

        Long customerId = customer.getId();

        return orderMapper.findMyOrderIds(cond, customerId);
    }

    @Override
    public List<MyOrderFlatDto> findMyOrders(List<Long> orderIds) {
        return orderMapper.findMyOrders(orderIds);
    }
}
