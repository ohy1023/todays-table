package store.myproject.onlineshop.domain.item.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;

@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCustomRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ItemDto> search(ItemSearchCond itemSearchCond, Pageable pageable) {


        return null;
    }
}
