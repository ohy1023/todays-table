package store.myproject.onlineshop.repository.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;

public interface ItemCustomRepository {
    Page<SimpleItemDto> search(ItemSearchCond itemSearchCond, Pageable pageable);
}
