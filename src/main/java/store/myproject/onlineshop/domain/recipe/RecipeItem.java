package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.item.Item;

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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public static RecipeItem createRecipeItem(Item item) {
        return RecipeItem.builder()
                .item(item)
                .build();
    }

}