package store.myproject.onlineshop.dto.recipestep;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeStepDto {

    private int stepOrder;
    private String content;
    private String imageUrl;
}
