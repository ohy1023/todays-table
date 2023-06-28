package store.myproject.onlineshop.domain.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;

public interface ItemCustomRepository {
    Page<ItemDto> search(ItemSearchCond itemSearchCond, Pageable pageable);
}
