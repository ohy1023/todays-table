package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.domain.order.dto.MyOrderFlatDto;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

import java.util.List;

@Mapper
public interface OrderMapper {
    List<Long> findMyOrderIds(@Param("cond") OrderSearchCond cond, @Param("customerId") Long customerId);

    List<MyOrderFlatDto> findMyOrders(@Param("orderIds") List<Long> orderIds);

}
