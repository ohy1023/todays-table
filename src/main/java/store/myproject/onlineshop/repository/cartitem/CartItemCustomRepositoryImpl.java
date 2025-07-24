package store.myproject.onlineshop.repository.cartitem;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.mapper.CartItemMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CartItemCustomRepositoryImpl implements CartItemCustomRepository {

    private final CartItemMapper cartItemMapper;

    @Override
    public Page<CartItemResponse> findByCartPage(Cart cart, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        List<CartItemResponse> cartItemResponses = cartItemMapper.findByCartPage(cart.getId());

        PageInfo<CartItemResponse> pageInfo = new PageInfo<>(cartItemResponses);

        return new PageImpl<>(cartItemResponses, pageable, pageInfo.getTotal());

    }
}
