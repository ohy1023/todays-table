package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.item.Item;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE recipe_item SET deleted_date = CURRENT_TIMESTAMP WHERE recipe_item_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(name = "recipe_item")
public class RecipeItem extends BaseEntity {

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