package store.myproject.onlineshop.domain.recipestep;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.recipe.Recipe;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE recipe_step SET deleted_date = CURRENT_TIMESTAMP WHERE recipe_step_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(name = "recipe_step")
public class RecipeStep extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_step_id")
    private Long id;

    @Column(nullable = false, name = "step_order")
    private int stepOrder;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "image_url")
    private String imageUrl;  // S3 URL (nullable)

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public static RecipeStep create(String content, String imageUrl, int stepOrder) {
        return RecipeStep.builder()
                .content(content)
                .imageUrl(imageUrl)
                .stepOrder(stepOrder)
                .build();
    }
}
