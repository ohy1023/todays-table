package store.myproject.onlineshop.domain.like;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@Table(name = "like_recipe")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    // 좋아요를 누른 고객
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // 좋아요를 받은 레시피
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    /**
     * 지정된 고객과 레시피로 새로운 Like 인스턴스를 생성합니다.
     *
     * @param customer 좋아요를 누른 고객
     * @param recipe   좋아요를 받은 레시피
     * @return 새로운 Like 인스턴스
     */
    public static Like of(Customer customer, Recipe recipe) {
        return Like.builder()
                .customer(customer)
                .recipe(recipe)
                .build();
    }

}
