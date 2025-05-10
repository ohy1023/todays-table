package store.myproject.onlineshop.domain.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레시피 생성 요청 DTO")
public class RecipeCreateRequest {

    @NotBlank
    @Schema(description = "레시피 제목", example = "매콤한 떡볶이")
    private String recipeTitle;

    @NotBlank
    @Schema(description = "레시피 설명", example = "쫄깃한 떡과 매콤한 소스의 조화")
    private String recipeDescription;

    @NotNull
    @Schema(description = "조리 시간", example = "30")
    private Integer recipeCookingTime;

    @NotNull
    @Schema(description = "인분 수", example = "2")
    private Integer recipeServings;

    @NotEmpty
    @Schema(description = "사용되는 아이템 UUID 목록", example = "[\"f47ac10b-58cc-4372-a567-0e02b2c3d479\"]")
    private List<UUID> itemUuidList;

    @NotEmpty
    @Schema(description = "레시피 단계 목록")
    private List<RecipeStepRequest> steps;

    @Schema(description = "썸네일 이미지 URL", example = "https://s3.bucket/image.jpg")
    private String thumbnailUrl;

    public Recipe toEntity(Customer customer) {
        return Recipe.builder()
                .uuid(UUIDGenerator.generateUUIDv7())
                .recipeTitle(recipeTitle)
                .recipeDescription(recipeDescription)
                .customer(customer)
                .recipeCookingTime(recipeCookingTime)
                .recipeServings(recipeServings)
                .recipeMeta(RecipeMeta.builder()
                        .reviewCnt(0L)
                        .likeCnt(0L)
                        .viewCnt(0L)
                        .build())
                .build();
    }
}
