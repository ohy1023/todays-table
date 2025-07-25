package store.myproject.onlineshop.domain.recipestep;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.recipe.Recipe;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecipeStep {

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
