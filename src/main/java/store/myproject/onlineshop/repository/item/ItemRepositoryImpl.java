package store.myproject.onlineshop.repository.item;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;
import store.myproject.onlineshop.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCustomRepository {

    private final ItemMapper itemMapper;

    @Override
    public Page<SimpleItemDto> searchItem(ItemSearchCond cond, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            PageHelper.orderBy(orderBy);
        }

        List<SimpleItemDto> simpleItemDtos = itemMapper.searchItem(cond);

        PageInfo<SimpleItemDto> pageInfo = new PageInfo<>(simpleItemDtos);

        return new PageImpl<>(simpleItemDtos, pageable, pageInfo.getTotal());
    }
}
