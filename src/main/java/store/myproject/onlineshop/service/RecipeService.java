package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.like.repository.LikeRepository;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.repository.RecipeRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;

import java.util.Optional;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {
    private final LikeRepository likeRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;

    /**
     * 특정 레시피에 대한 좋아요를 토글하는 메서드입니다.
     *
     * @param recipeId 레시피 ID
     * @param email    사용자 이메일
     * @return MessageResponse
     */
    public MessageResponse pushLike(Long recipeId, String email) {
        // 이메일을 통해 현재 로그인한 회원을 검증합니다.
        Customer customer = validateByEmail(email);

        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 레시피와 회원에 대한 좋아요가 이미 존재하는지 확인합니다.
        Optional<Like> optionalLike = likeRepository.findByRecipeAndCustomer(recipe, customer);

        if (optionalLike.isPresent()) {
            // 좋아요가 이미 존재하면 취소하고 메시지 응답을 반환합니다.
            likeRepository.delete(optionalLike.get());
            return new MessageResponse("좋아요를 취소합니다.");
        } else {
            // 좋아요가 존재하지 않으면 생성하고 메시지 응답을 반환합니다.
            likeRepository.save(Like.of(customer, recipe));
            return new MessageResponse("좋아요를 눌렀습니다.");
        }
    }


    /**
     * 레시피 ID를 이용하여 현재 조회하고자 하는 레시피의 좋아요 개수를 반환하는 메서드입니다.
     *
     * @param recipeId 레시피 ID
     * @return Integer 좋아요 개수
     * @throws AppException 해당 ID에 대한 레시피가 존재하지 않을 경우 발생
     **/
    public Integer countLike(Long recipeId) {
        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);
        return likeRepository.countByRecipe(recipe);
    }

    /**
     * 이메일을 이용하여 현재 로그인한 회원이 존재하는지 검증하는 메서드입니다.
     *
     * @param email 사용자 이메일
     * @return Customer 객체
     * @throws AppException 해당 이메일에 대한 회원이 존재하지 않을 경우 발생
     */
    private Customer validateByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));
    }

    /**
     * 레시피 ID를 이용하여 현재 조회하고자 하는 레시피가 존재하는지 검증하는 메서드입니다.
     *
     * @param id 레시피 ID
     * @return Recipe 객체
     * @throws AppException 해당 ID에 대한 레시피가 존재하지 않을 경우 발생
     */
    private Recipe validateByRecipe(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND, ErrorCode.RECIPE_NOT_FOUND.getMessage()));
    }

}
