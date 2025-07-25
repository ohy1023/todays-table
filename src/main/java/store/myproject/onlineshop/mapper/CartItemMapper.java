package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;

import java.util.List;

@Mapper
public interface CartItemMapper {

    List<CartItemResponse> findByCartPage(@Param("cartId") Long cartId);
}
