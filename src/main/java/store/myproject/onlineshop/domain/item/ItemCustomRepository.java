package store.myproject.onlineshop.domain.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.dto.item.ItemSearchCond;
import store.myproject.onlineshop.dto.item.SimpleItemDto;

public interface ItemCustomRepository {
    Page<SimpleItemDto> searchItem(ItemSearchCond itemSearchCond, Pageable pageable);
}
