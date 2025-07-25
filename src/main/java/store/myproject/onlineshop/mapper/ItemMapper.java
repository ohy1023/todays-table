package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;

import java.util.List;

@Mapper
public interface ItemMapper {
    List<SimpleItemDto> searchItem(@Param("cond")ItemSearchCond cond);
}
