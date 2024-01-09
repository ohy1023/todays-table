package store.myproject.onlineshop.domain.recipeitem;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.math.BigDecimal;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public static RecipeItem createRecipeItem(Item item) {

        return RecipeItem.builder()
                .item(item)
                .build();
    }

    // 연관 관계 편의 메서드
    public void setRecipeAndItem(Recipe recipe, Item item) {
        this.recipe = recipe;
        recipe.getItemList().add(this);  // 연관된 Recipe에도 RecipeItem을 추가
        //
        this.item = item;
        item.getRecipeItemList().add(this);  // 연관된 Item에도 RecipeItem을 추가
    }

}