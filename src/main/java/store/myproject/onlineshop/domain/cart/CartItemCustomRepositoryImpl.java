package store.myproject.onlineshop.domain.cart;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.dto.cart.CartItemResponse;
import store.myproject.onlineshop.mapper.CartItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartItemCustomRepositoryImpl implements CartItemCustomRepository {

    private final CartItemMapper cartItemMapper;

    @Override
    public Page<CartItemResponse> findByCartPage(Cart cart, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            PageHelper.orderBy(orderBy);
        }

        List<CartItemResponse> cartItemResponses = cartItemMapper.findByCartPage(cart.getId());

        PageInfo<CartItemResponse> pageInfo = new PageInfo<>(cartItemResponses);

        return new PageImpl<>(cartItemResponses, pageable, pageInfo.getTotal());

    }
}
