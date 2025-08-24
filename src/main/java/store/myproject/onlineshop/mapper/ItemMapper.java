package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.dto.item.ItemSearchCond;
import store.myproject.onlineshop.dto.item.SimpleItemDto;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<SimpleItemDto> searchItem(@Param("cond")ItemSearchCond cond);

    Long countItem(@Param("cond")ItemSearchCond cond);
}
