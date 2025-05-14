package store.myproject.onlineshop.domain.recipemeta.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeMetaDto {

    private Long viewCnt;
    private Long reviewCnt;
    private Long likeCnt;

}
