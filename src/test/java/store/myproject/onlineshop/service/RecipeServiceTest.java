package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockMultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.domain.review.dto.ChildReviewResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteRequest;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.*;
import store.myproject.onlineshop.global.utils.MessageUtil;
import store.myproject.onlineshop.global.utils.RedisKeyHelper;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.repository.like.LikeRepository;
import store.myproject.onlineshop.repository.recipe.RecipeRepository;
import store.myproject.onlineshop.repository.recipeitem.RecipeItemRepository;
import store.myproject.onlineshop.repository.recipestep.RecipeStepRepository;
import store.myproject.onlineshop.repository.review.ReviewRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private MessageUtil messageUtil;
    @Mock
    private AwsS3Service awsS3Service;
    @Mock
    private RecipeMetaService recipeMetaService;
    @Mock
    private RecipeStepRepository recipeStepRepository;
    @Mock
    private RecipeItemRepository recipeItemRepository;
    @Mock
    private RedisTemplate<String, Object> cacheRedisTemplate;
    @Mock
    private ValueOperations<String, Object> valueOperations;
    @Mock
    private RedissonClient redisson;
    @Mock
    private RLock rLock;

    Customer customer = CustomerFixture.createCustomer();
    Brand brand = BrandFixture.createBrandEntity();
    Item item = ItemFixture.createItemEntity(brand);
    Recipe recipe = RecipeFixture.createRecipeEntity(customer);
    Review review = ReviewFixture.createParentReviewEntity(recipe, customer);

    @Test
    @DisplayName("레시피 상세 조회 성공 - 캐시 히트")
    void get_recipe_detail_success_by_cache_hit() {
        // given
        Long recipeId = 1L;
        UUID recipeUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        RecipeDto dto = RecipeFixture.createRecipeDto(recipeUuid);
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);
        given(cacheRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(cacheRedisTemplate.opsForValue().get(recipeCacheKey)).willReturn(dto);
        given(recipeRepository.findRecipeMetaIdByRecipeUuid(recipeUuid)).willReturn(recipeId);

        // when
        RecipeDto result = recipeService.getRecipeDetail(recipeUuid);

        // then
        assertThat(result).isEqualTo(dto);
        then(recipeMetaService).should(times(1)).asyncIncreaseViewCnt(recipeId);
        then(recipeRepository).should(never()).findRecipeDtoByUuid(any());
        then(recipeStepRepository).shouldHaveNoInteractions();
        then(recipeItemRepository).shouldHaveNoInteractions();
        then(redisson).should(never()).getLock(anyString());
    }

    @Test
    @DisplayName("레시피 상세 조회 성공 - 캐시 미스")
    void get_recipe_detail_success_by_cache_miss() throws InterruptedException {
        // given
        Long recipeId = 1L;
        UUID recipeUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        RecipeDto dto = RecipeFixture.createRecipeDto(recipeUuid);
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);
        String recipeLockKey = RedisKeyHelper.getRecipeLockKey(recipeUuid);
        given(cacheRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(cacheRedisTemplate.opsForValue().get(recipeCacheKey)).willReturn(null);
        given(redisson.getLock(recipeLockKey)).willReturn(rLock);
        given(rLock.tryLock(300,2000, TimeUnit.MILLISECONDS)).willReturn(true);
        given(cacheRedisTemplate.opsForValue().get(recipeCacheKey)).willReturn(null);
        given(recipeRepository.findRecipeDtoByUuid(recipeUuid)).willReturn(Optional.of(dto));
        given(recipeStepRepository.findStepsByRecipeUuid(recipeUuid)).willReturn(List.of());
        given(recipeItemRepository.findItemsByRecipeUuid(recipeUuid)).willReturn(List.of());
        given(recipeRepository.findRecipeMetaIdByRecipeUuid(recipeUuid)).willReturn(recipeId);

        // when
        RecipeDto result = recipeService.getRecipeDetail(recipeUuid);

        // then
        assertThat(result).isEqualTo(dto);
        then(recipeMetaService).should(times(1)).asyncIncreaseViewCnt(recipeId);
        then(cacheRedisTemplate.opsForValue()).should().set(eq(recipeCacheKey), eq(dto), any());
        then(rLock).should().unlock();
    }

    @Test
    @DisplayName("레시피 생성 성공")
    void create_recipe_success() {
        // given
        RecipeCreateRequest request = RecipeFixture.createRecipeCreateRequest();
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(itemRepository.findByUuid(any())).willReturn(Optional.of(item));
        given(messageUtil.get(MessageCode.RECIPE_ADDED)).willReturn("레시피 등록 성공");

        // when
        MessageResponse response = recipeService.createRecipe(request, customer.getEmail());

        // then
        then(recipeRepository).should().save(any());
        assertThat(response.getMessage()).isEqualTo("레시피 등록 성공");
    }

    @Test
    @DisplayName("레시피 목록 조회 성공 - 슬라이스 반환")
    void get_recipes_success() {
        // given
        Pageable pageable = PageRequest.of(0, 3);

        List<SimpleRecipeDto> recipes = List.of(
                RecipeFixture.createSimpleRecipeDto(),
                RecipeFixture.createSimpleRecipeDto()
        );
        Slice<SimpleRecipeDto> slice = new SliceImpl<>(recipes, pageable, false);

        given(recipeRepository.findAllSimpleRecipes(pageable)).willReturn(slice);

        // when
        Slice<SimpleRecipeDto> result = recipeService.getRecipes(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("특정 아이템을 사용하는 레시피 조회 성공")
    void get_recipes_by_item_success() {
        UUID itemUuid = UUID.randomUUID();

        // given
        Pageable pageable = PageRequest.of(0, 5);
        Page<SimpleRecipeDto> page = new PageImpl<>(List.of(
                RecipeFixture.createSimpleRecipeDto()
        ));

        given(recipeRepository.findRecipeUseItem(itemUuid, pageable)).willReturn(page);

        // when
        Page<SimpleRecipeDto> result = recipeService.getRecipesByItem(itemUuid, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRecipeServings()).isEqualTo(1);
    }

    @Test
    @DisplayName("레시피 수정 성공 - 기존 재료/단계 초기화 후 덮어쓰기")
    void update_recipe_success() {
        // given
        RecipeUpdateRequest request = RecipeFixture.createRecipeUpdateRequest();
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipe.getUuid());
        recipe.addItems(List.of(RecipeItem.createRecipeItem(item)));
        recipe.addSteps(List.of(RecipeStep.builder().stepOrder(1).content("구").build()));

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(itemRepository.findByUuid(any())).willReturn(Optional.of(item));
        given(messageUtil.get(MessageCode.RECIPE_MODIFIED)).willReturn("레시피 수정 완료");

        // when
        MessageResponse response = recipeService.updateRecipe(recipe.getUuid(), request, customer.getEmail());

        // then
        then(cacheRedisTemplate).should(times(1)).delete(recipeCacheKey);
        assertThat(response.getMessage()).isEqualTo("레시피 수정 완료");
    }

    @Test
    @DisplayName("레시피 삭제 성공")
    void delete_recipe_success() {
        // given
        UUID recipeUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByUuid(recipeUuid)).willReturn(Optional.of(recipe));
        given(messageUtil.get(MessageCode.RECIPE_DELETED)).willReturn("삭제 성공");

        // when
        MessageResponse response = recipeService.deleteRecipe(recipeUuid, customer.getEmail());

        // then
        then(recipeRepository).should().delete(recipe);

        then(cacheRedisTemplate).should(times(1)).delete(recipeCacheKey);
        assertThat(response.getMessage()).isEqualTo("삭제 성공");
    }

    @Test
    @DisplayName("레시피 이미지 업로드 성공 - S3 연동")
    void upload_image_success() {
        // given
        MockMultipartFile mockFile = CommonFixture.mockMultipartFile();
        given(awsS3Service.uploadRecipeOriginImage(mockFile)).willReturn("https://s3.bucket/recipe/image.jpg");

        // when
        MessageResponse response = recipeService.uploadImage(mockFile);

        // then
        assertThat(response.getMessage()).isEqualTo("https://s3.bucket/recipe/image.jpg");
    }

    @Test
    @DisplayName("레시피 좋아요 성공 → 좋아요가 없을 때")
    void toggle_like_add_success() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByIdWithMeta(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(likeRepository.findByRecipeAndCustomer(recipe, customer)).willReturn(Optional.empty());
        given(messageUtil.get(MessageCode.DO_LIKE)).willReturn("좋아요 등록");

        // when
        MessageResponse response = recipeService.toggleLike(recipe.getUuid(), customer.getEmail());

        // then
        then(likeRepository).should().save(any());
        then(recipeMetaService).should().asyncIncreaseLikeCnt(recipe.getRecipeMeta().getId());
        assertThat(response.getMessage()).isEqualTo("좋아요 등록");
    }

    @Test
    @DisplayName("레시피 좋아요 취소 성공 → 좋아요가 이미 있을 때")
    void toggle_like_remove_success() {
        // given
        Like like = Like.of(customer, recipe);
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByIdWithMeta(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(likeRepository.findByRecipeAndCustomer(recipe, customer)).willReturn(Optional.of(like));
        given(messageUtil.get(MessageCode.UNDO_LIKE)).willReturn("좋아요 취소");

        // when
        MessageResponse response = recipeService.toggleLike(recipe.getUuid(), customer.getEmail());

        // then
        then(likeRepository).should().delete(like);
        then(recipeMetaService).should().asyncDecreaseLikeCnt(recipe.getRecipeMeta().getId());
        assertThat(response.getMessage()).isEqualTo("좋아요 취소");
    }

    @Test
    @DisplayName("레시피 리뷰 목록 조회 성공")
    void get_recipe_reviews_success() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> parentPage = new PageImpl<>(List.of(review));
        List<Review> childReviews = List.of();
        Map<Long, List<Review>> childMap = Map.of(review.getId(), childReviews);
        Map<Long, Long> countMap = Map.of(review.getId(), 0L);

        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findParentReviews(recipe, pageable)).willReturn(parentPage);
        given(reviewRepository.findTop3ChildReviews(List.of(review.getId()), PageRequest.of(0, 3))).willReturn(childReviews);
        given(reviewRepository.countByParentIds(List.of(review.getId()))).willReturn(countMap);

        // when
        Page<ReviewResponse> result = recipeService.getRecipeReviews(recipe.getUuid(), pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo(review.getReviewContent());
    }

    @Test
    @DisplayName("대댓글 조회 성공")
    void get_child_reviews_success() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Review parent = ReviewFixture.createParentReviewEntity(recipe, customer);
        Review child1 = ReviewFixture.createChildReviewEntity(recipe, customer, parent);
        Page<Review> children = new PageImpl<>(List.of(child1));

        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findByUuid(parent.getUuid())).willReturn(Optional.of(parent));
        given(reviewRepository.findByParentId(parent.getId(), pageable)).willReturn(children);

        // when
        Page<ChildReviewResponse> result = recipeService.getChildReviews(
                recipe.getUuid(), parent.getUuid(), pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo(child1.getReviewContent());
    }

    @Test
    @DisplayName("대댓글 조회 실패 - 부모 리뷰가 레시피에 속하지 않음")
    void get_child_reviews_fail_if_review_not_belongs_to_recipe() {
        // given
        Recipe otherRecipe = RecipeFixture.createRecipeEntityWithId(2L, customer);
        Review invalidParent = ReviewFixture.createParentReviewEntity(otherRecipe, customer);
        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findByUuid(invalidParent.getUuid())).willReturn(Optional.of(invalidParent));

        // when & then
        assertThatThrownBy(() -> recipeService.getChildReviews(
                recipe.getUuid(), invalidParent.getUuid(), PageRequest.of(0, 5)))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.INVALID_REVIEW.getMessage());
    }

    @Test
    @DisplayName("리뷰 작성 성공")
    void create_review_success() {
        // given
        ReviewWriteRequest request = ReviewFixture.createReviewWriteRequest();
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByIdWithMeta(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(messageUtil.get(MessageCode.RECIPE_REVIEW_ADDED)).willReturn("리뷰 등록");

        // when
        MessageResponse response = recipeService.createReview(customer.getEmail(), recipe.getUuid(), request);

        // then
        then(reviewRepository).should().save(any(Review.class));
        then(recipeMetaService).should().asyncIncreaseReviewCnt(recipe.getRecipeMeta().getId());
        assertThat(response.getMessage()).isEqualTo("리뷰 등록");
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void update_review_success() {
        // given
        ReviewUpdateRequest request = ReviewFixture.createReviewUpdateRequest();

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findByUuid(review.getUuid())).willReturn(Optional.of(review));
        given(messageUtil.get(MessageCode.RECIPE_REVIEW_MODIFIED)).willReturn("리뷰 수정 성공");

        // when
        MessageResponse response = recipeService.updateReview(customer.getEmail(), recipe.getUuid(), review.getUuid(), request);

        // then
        assertThat(response.getMessage()).isEqualTo("리뷰 수정 성공");
    }

    @Test
    @DisplayName("리뷰 수정 실패 - 작성자 불일치")
    void update_review_fail_if_not_author() {
        // given
        Customer other = CustomerFixture.createCustomer();
        Review otherReview = ReviewFixture.createParentReviewEntity(recipe, other);
        ReviewUpdateRequest request = new ReviewUpdateRequest("불법 수정 시도");

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByUuid(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findByUuid(otherReview.getUuid())).willReturn(Optional.of(otherReview));

        // when & then
        assertThatThrownBy(() -> recipeService.updateReview(customer.getEmail(), recipe.getUuid(), otherReview.getUuid(), request))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.FORBIDDEN_ACCESS.getMessage());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void delete_review_success() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(recipeRepository.findByIdWithMeta(recipe.getUuid())).willReturn(Optional.of(recipe));
        given(reviewRepository.findByUuid(review.getUuid())).willReturn(Optional.of(review));
        given(messageUtil.get(MessageCode.RECIPE_REVIEW_DELETED)).willReturn("리뷰 삭제");

        // when
        MessageResponse response = recipeService.deleteReview(customer.getEmail(), recipe.getUuid(), review.getUuid());

        // then
        then(reviewRepository).should().delete(review);
        then(recipeMetaService).should().asyncDecreaseReviewCnt(recipe.getRecipeMeta().getId());
        assertThat(response.getMessage()).isEqualTo("리뷰 삭제");
    }

}
