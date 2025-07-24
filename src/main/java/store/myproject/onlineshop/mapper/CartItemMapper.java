package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;

import java.util.List;

@Mapper
public interface CartItemMapper {

    List<CartItemResponse> findByCartPage(Long cartId);
}
