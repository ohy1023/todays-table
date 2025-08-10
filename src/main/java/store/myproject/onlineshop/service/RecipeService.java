package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLog;
import store.myproject.onlineshop.domain.faillog.JobType;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;
import store.myproject.onlineshop.domain.recipemeta.dto.RecipeMetaDto;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;
import store.myproject.onlineshop.domain.review.dto.*;
import store.myproject.onlineshop.global.utils.RedisKeyHelper;
import store.myproject.onlineshop.repository.asyncFailureLog.AsyncFailureLogRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.repository.like.LikeRepository;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.repository.recipe.RecipeRepository;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.repository.recipeitem.RecipeItemRepository;
import store.myproject.onlineshop.repository.recipemeta.RecipeMetaRepository;
import store.myproject.onlineshop.repository.recipestep.RecipeStepRepository;
import store.myproject.onlineshop.repository.review.ReviewRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static store.myproject.onlineshop.domain.faillog.FailureStatus.*;
import static store.myproject.onlineshop.domain.faillog.JobType.*;
import static store.myproject.onlineshop.exception.ErrorCode.CUSTOMER_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.FORBIDDEN_ACCESS;
import static store.myproject.onlineshop.exception.ErrorCode.INVALID_REVIEW;
import static store.myproject.onlineshop.exception.ErrorCode.ITEM_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.RECIPE_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.REVIEW_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeService {

    private final LikeRepository likeRepository;
    private final CustomerRepository customerRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MessageUtil messageUtil;
    private final AwsS3Service awsS3Service;
    private final RecipeMetaService recipeMetaService;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeItemRepository recipeItemRepository;
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final RedissonClient redisson;
    private final RecipeMetaRepository recipeMetaRepository;
    private final AsyncFailureLogRepository asyncFailureLogRepository;

    /**
     * 단일 레시피 정보를 조회하고 조회수를 증가시킵니다.
     */
    @Transactional(readOnly = true)
    public RecipeDto getRecipeDetail(UUID recipeUuid) {
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);

        // 1. 캐시에서 데이터 조회
        RecipeDto cachedRecipe = (RecipeDto) cacheRedisTemplate.opsForValue().get(recipeCacheKey);
        if (cachedRecipe != null) {
            return cachedRecipe;
        }

        // 2. 캐시에 없으면 락 획득 후 다시 확인 및 저장
        String recipeLockKey = RedisKeyHelper.getRecipeLockKey(recipeUuid);
        RLock lock = redisson.getLock(recipeLockKey);

        try {
            boolean isLocked = lock.tryLock(300, 2000, TimeUnit.MILLISECONDS);

            if (isLocked) {
                try {
                    // 캐시 재확인 (다른 스레드가 락을 선점하여 캐싱했을 수도 있음)
                    RecipeDto doubleCheckCache = (RecipeDto) cacheRedisTemplate.opsForValue().get(recipeCacheKey);
                    if (doubleCheckCache != null) {
                        return doubleCheckCache;
                    }

                    // DB 조회
                    RecipeDto recipeDto = recipeRepository.findRecipeDtoByUuid(recipeUuid)
                            .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND));

                    List<RecipeStepDto> stepDtos = recipeStepRepository.findStepsByRecipeUuid(recipeUuid);
                    List<RecipeItemDto> itemDtos = recipeItemRepository.findItemsByRecipeUuid(recipeUuid);

                    recipeDto.setSteps(stepDtos);
                    recipeDto.setItems(itemDtos);

                    // 캐시에 저장 (1일 유지)
                    cacheRedisTemplate.opsForValue().set(recipeCacheKey, recipeDto, Duration.ofDays(1L));

                    return recipeDto;
                } finally {
                    lock.unlock();
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(100); // 100ms 대기
                    RecipeDto retryCache = (RecipeDto) cacheRedisTemplate.opsForValue().get(recipeCacheKey);
                    if (retryCache != null) {
                        return retryCache;
                    }
                }
                throw new AppException(RECIPE_NOT_FOUND);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // todo 커스텀 예외 처리
            throw new RuntimeException("Thread interrupted during lock acquisition", e);
        }

    }

    /**
     * 레시피 통계 정보(조회수, 좋아요 수, 댓글 수) 조회
     */
    @Transactional(readOnly = true)
    public RecipeMetaDto getRecipeMeta(UUID recipeUuid) {
        Recipe recipe = recipeRepository.findByUuid(recipeUuid)
                .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND));

        return recipeMetaRepository.findRecipeMetaDto(recipe.getId());
    }

    /**
     * 레시피 목록 조회.
     */
    @Transactional(readOnly = true)
    public RecipeCursorResponse getRecipes(RecipeListCond cond) {

        List<SimpleRecipeDto> recipes = recipeRepository.findRecipeList(cond);

        boolean hasNext = recipes.size() > cond.getSize();

        List<SimpleRecipeDto> limitedRecipes = hasNext
                ? recipes.subList(0, cond.getSize())
                : recipes;

        UUID nextUuid = hasNext
                ? recipes.get(recipes.size() - 1).getRecipeUuid()
                : null;

        Long nextViewCount = hasNext
                ? recipes.get(recipes.size() - 1).getRecipeView()
                : null;

        Long nextLikeCount = hasNext
                ? recipes.get(recipes.size() - 1).getLikeCnt()
                : null;

        return RecipeCursorResponse.of(limitedRecipes, nextUuid, nextViewCount, nextLikeCount);
    }

    /**
     * 레시피를 등록합니다. 재료 및 단계, 썸네일까지 포함됩니다.
     */
    public MessageResponse createRecipe(RecipeCreateRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = request.toEntity(customer);
        recipe.addItems(mapToRecipeItems(request.getItemUuidList()));
        recipe.addSteps(mapToRecipeSteps(request.getSteps()));
        applyThumbnail(recipe, request.getThumbnailUrl());
        recipeRepository.save(recipe);
        return MessageResponse.of(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_ADDED));
    }

    /**
     * 레시피를 수정합니다. 모든 정보는 덮어쓰기 방식으로 갱신됩니다.
     */
    public MessageResponse updateRecipe(UUID recipeUuid, RecipeUpdateRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeByUuid(recipeUuid);
        validatePermission(customer, recipe.getCustomer());
        recipe.updateRecipe(request);
        recipe.getItemList().clear();
        recipe.getStepList().clear();
        recipe.addItems(mapToRecipeItems(request.getItemUuidList()));
        recipe.addSteps(mapToRecipeSteps(request.getSteps()));
        applyThumbnail(recipe, request.getThumbnailUrl());

        // 캐시 무효화
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);
        cacheRedisTemplate.delete(recipeCacheKey);

        return MessageResponse.of(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_MODIFIED));
    }

    /**
     * 레시피를 삭제합니다. 관리자 또는 작성자만 삭제할 수 있습니다.
     */
    public MessageResponse deleteRecipe(UUID recipeUuid, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeByUuid(recipeUuid);
        validatePermission(customer, recipe.getCustomer());
        recipeRepository.delete(recipe);

        // 캐시 무효화
        String recipeCacheKey = RedisKeyHelper.getRecipeKey(recipeUuid);
        cacheRedisTemplate.delete(recipeCacheKey);

        return MessageResponse.of(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_DELETED));
    }

    /**
     * 해당 레시피에 작성된 댓글과 대댓글 일부를 조회합니다.
     */
    public Page<ReviewResponse> getRecipeReviews(UUID recipeUuid, Pageable pageable) {
        Recipe recipe = getRecipeByUuid(recipeUuid);
        Page<Review> parents = reviewRepository.findParentReviews(recipe, pageable);
        List<Long> parentIds = parents.stream().map(Review::getId).toList();
        Map<Long, List<Review>> childMap = reviewRepository.findTop3ChildReviews(parentIds, PageRequest.of(0, 3))
                .stream().collect(Collectors.groupingBy(Review::getParentId));
        Map<Long, Long> childCountMap = reviewRepository.countByParentIds(parentIds);
        return parents.map(parent -> toReviewResponse(parent, childMap, childCountMap));
    }

    /**
     * 특정 댓글의 모든 대댓글을 조회합니다.
     */
    public Page<ChildReviewResponse> getChildReviews(UUID recipeUuid, UUID reviewUuid, Pageable pageable) {
        getRecipeByUuid(recipeUuid);
        Review parent = getReviewByUuid(reviewUuid);
        if (!parent.getRecipe().getUuid().equals(recipeUuid)) {
            throw new AppException(INVALID_REVIEW);
        }
        return reviewRepository.findByParentId(parent.getId(), pageable)
                .map(this::toChildReviewDto);
    }

    /**
     * 댓글 또는 대댓글을 작성합니다.
     */
    public MessageResponse createReview(String email, UUID recipeUuid, ReviewWriteRequest request) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeWithMeta(recipeUuid);

        Long parentId = null;
        if (request.getReviewUuid() != null) {
            Review parentReview = reviewRepository.findByUuid(request.getReviewUuid())
                    .orElseThrow(() -> new AppException(REVIEW_NOT_FOUND));
            parentId = parentReview.getId();
        }

        Review review = request.toEntity(parentId, request.getReviewContent(), customer, recipe);
        review.addReviewToRecipe(recipe);
        reviewRepository.save(review);
        return MessageResponse.of(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_ADDED));
    }

    /**
     * 댓글을 수정합니다.
     */
    public MessageResponse updateReview(String email, UUID recipeUuid, UUID reviewUuid, ReviewUpdateRequest request) {
        Customer customer = getCustomerByEmail(email);
        getRecipeByUuid(recipeUuid);
        Review review = getReviewByUuid(reviewUuid);
        validatePermission(customer, review.getCustomer());
        review.updateReview(request);
        return MessageResponse.of(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_MODIFIED));
    }

    /**
     * 댓글을 삭제합니다. 작성자 또는 관리자만 가능합니다.
     */
    public MessageResponse deleteReview(String email, UUID recipeUuid, UUID reviewUuid) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeWithMeta(recipeUuid);
        Review review = getReviewByUuid(reviewUuid);
        validatePermission(customer, review.getCustomer());
        review.removeReviewToRecipe();
        reviewRepository.delete(review);
        return MessageResponse.of(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_DELETED));
    }

    /**
     * 좋아요 토글 처리합니다. 이미 눌렀으면 삭제, 아니면 추가.
     */
    public MessageResponse toggleLike(UUID recipeUuid, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeWithMeta(recipeUuid);
        Optional<Like> like = likeRepository.findByRecipeAndCustomer(recipe, customer);
        Long recipeMetaId = recipe.getRecipeMeta().getId();
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            decreaseLikeCount(recipeMetaId);
            recipeMetaService.asyncDecreaseLikeCnt(recipeMetaId);
            return MessageResponse.of(messageUtil.get(MessageCode.UNDO_LIKE));
        }
        likeRepository.save(Like.of(customer, recipe));
        increaseLikeCount(recipeMetaId);
        recipeMetaService.asyncIncreaseLikeCnt(recipeMetaId);

        return MessageResponse.of(messageUtil.get(MessageCode.DO_LIKE));
    }

    /**
     * 레시피 단계 이미지 업로드
     */
    public MessageResponse uploadImage(MultipartFile file) {
        return MessageResponse.of(awsS3Service.uploadRecipeOriginImage(file));
    }

    /**
     * 레시피 조회 수 증가
     */
    public void increaseRecipeViewCount(UUID recipeUuid) {
        Long recipeMetaId = recipeRepository.findRecipeMetaIdByRecipeUuid(recipeUuid);
        try {
            recipeMetaService.asyncIncreaseViewCnt(recipeMetaId);
        } catch (RejectedExecutionException e) {
            saveAsyncFailureLog(e, recipeMetaId, RECIPE_VIEW_COUNT_INCREMENT);
        }
    }

    /**
     * 리뷰 수 증가
     */
    public void increaseReviewCount(UUID recipeUuid) {
        Long recipeMetaId = recipeRepository.findRecipeMetaIdByRecipeUuid(recipeUuid);
        try {
            recipeMetaService.asyncIncreaseReviewCnt(recipeMetaId);
        } catch (RejectedExecutionException e) {
            saveAsyncFailureLog(e, recipeMetaId, REVIEW_COUNT_INCREMENT);
        }
    }

    /**
     * 리뷰 수 감소
     */
    public void decreaseReviewCount(UUID recipeUuid) {
        Long recipeMetaId = recipeRepository.findRecipeMetaIdByRecipeUuid(recipeUuid);
        try {
            recipeMetaService.asyncDecreaseReviewCnt(recipeMetaId);
        } catch (RejectedExecutionException e) {
            saveAsyncFailureLog(e, recipeMetaId, REVIEW_COUNT_DECREMENT);
        }
    }

    /**
     * 리뷰 수 증가
     */
    public void increaseLikeCount(Long recipeMetaId) {
        try {
            recipeMetaService.asyncIncreaseLikeCnt(recipeMetaId);
        } catch (RejectedExecutionException e) {
            saveAsyncFailureLog(e, recipeMetaId, LIKE_COUNT_INCREMENT);
        }
    }

    /**
     * 리뷰 수 감소
     */
    public void decreaseLikeCount(Long recipeMetaId) {
        try {
            recipeMetaService.asyncDecreaseLikeCnt(recipeMetaId);
        } catch (RejectedExecutionException e) {
            saveAsyncFailureLog(e, recipeMetaId, LIKE_COUNT_INCREMENT);
        }
    }

    /**
     * 특정 아이템을 사용하는 레시피 목록 조회
     */
    public Page<SimpleRecipeDto> getRecipesByItem(UUID itemUuid, Pageable pageable) {
        Long itemId = itemRepository.findIdByUuid(itemUuid).orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
        return recipeRepository.findRecipeUseItem(itemId, pageable);
    }

    /**
     * 아이템 ID 리스트로 RecipeItem 리스트 생성
     */
    private List<RecipeItem> mapToRecipeItems(List<UUID> itemUuids) {
        return itemUuids.stream()
                .map(this::getItemByUuid)
                .map(RecipeItem::createRecipeItem)
                .toList();
    }

    /**
     * 요청된 RecipeStepRequest 리스트로 RecipeStep 리스트 생성
     */
    private List<RecipeStep> mapToRecipeSteps(List<RecipeStepRequest> steps) {
        return steps.stream()
                .map(step -> RecipeStep.builder()
                        .stepOrder(step.getOrder())
                        .content(step.getContent())
                        .imageUrl(step.getImageUrl())
                        .build())
                .toList();
    }

    /**
     * 썸네일 URL이 있으면 그대로 설정하고, 없으면 첫 이미지로 대체
     */
    private void applyThumbnail(Recipe recipe, String thumbnailUrl) {
        if (thumbnailUrl != null) {
            recipe.setThumbnailUrl(thumbnailUrl);
        } else {
            recipe.getStepList().stream()
                    .map(RecipeStep::getImageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .findFirst()
                    .ifPresent(recipe::setThumbnailUrl);
        }
    }

    /**
     * 댓글을 ReviewResponse로 변환
     */
    private ReviewResponse toReviewResponse(Review parent, Map<Long, List<Review>> childMap, Map<Long, Long> countMap) {
        List<ChildReviewResponse> children = Optional.ofNullable(childMap.get(parent.getId()))
                .orElse(List.of())
                .stream()
                .map(this::toChildReviewDto)
                .toList();
        boolean hasMore = countMap.getOrDefault(parent.getId(), 0L) > 3;
        return ReviewResponse.builder()
                .uuid(parent.getUuid())
                .writer(parent.getCustomer().getNickName())
                .content(parent.getReviewContent())
                .childReviews(children)
                .hasMoreChildReviews(hasMore)
                .build();
    }

    /**
     * 대댓글을 ChildReviewResponse로 변환
     */
    private ChildReviewResponse toChildReviewDto(Review review) {
        return ChildReviewResponse.builder()
                .uuid(review.getUuid())
                .writer(review.getCustomer().getNickName())
                .content(review.getReviewContent())
                .build();
    }

    /**
     * 관리자 또는 작성자 여부 확인
     */
    private void validatePermission(Customer requester, Customer target) {
        if (!requester.equals(target) && requester.getCustomerRole() != CustomerRole.ROLE_ADMIN) {
            throw new AppException(FORBIDDEN_ACCESS);
        }
    }

    /**
     * ID로 레시피 조회
     */
    private Recipe getRecipeByUuid(UUID uuid) {
        return recipeRepository.findByUuid(uuid).orElseThrow(() -> new AppException(RECIPE_NOT_FOUND));
    }

    /**
     * 이메일로 고객 조회
     */
    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
    }

    /**
     * ID로 아이템 조회
     */
    private Item getItemByUuid(UUID uuid) {
        return itemRepository.findByUuid(uuid).orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
    }

    /**
     * ID로 리뷰 조회
     */
    private Review getReviewByUuid(UUID uuid) {
        return reviewRepository.findByUuid(uuid).orElseThrow(() -> new AppException(REVIEW_NOT_FOUND));
    }

    /**
     * 레시피 조회 (with. meta)
     */
    private Recipe getRecipeWithMeta(UUID recipeUuid) {
        return recipeRepository.findByIdWithMeta(recipeUuid).orElseThrow(() -> new AppException(RECIPE_NOT_FOUND));
    }

    private void saveAsyncFailureLog(RejectedExecutionException e, Long targetId, JobType jobType) {
        AsyncFailureLog asyncFailureLog = AsyncFailureLog.builder()
                .targetId(targetId)
                .jobType(jobType)
                .amount(null)
                .errorMessage(e.getMessage())
                .failureStatus(FAILED)
                .build();
        asyncFailureLogRepository.save(asyncFailureLog);
    }


    public Page<SimpleRecipeDto> testPage(Pageable pageable) {
        return recipeRepository.findRecipeVer1(pageable);
    }


    public Slice<SimpleRecipeDto> testSlice(Pageable pageable) {
        return recipeRepository.findRecipeVer2(pageable);
    }


//    public RecipeCursorResponse testCursor(RecipeCond cond) {
//
//        List<SimpleRecipeDto> recipes = recipeRepository.findRecipeVer3(cond);
//
//        boolean hasNext = recipes.size() > cond.getSize();
//
//        List<SimpleRecipeDto> limitedRecipes = hasNext
//                ? recipes.subList(0, cond.getSize())
//                : recipes;
//
//        UUID nextCursor = hasNext
//                ? recipes.get(recipes.size() - 1).getRecipeUuid()
//                : null;
//
//        return new RecipeCursorResponse(limitedRecipes, nextCursor);
//    }

    public Page<SimpleRecipeDto> testCountPer(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findRecipeVer5(pageable);

        List<SimpleRecipeDto> result = recipes.stream()
                .map(recipe -> {
                    Long likeCount = likeRepository.countByRecipe(recipe);
                    Long reviewCount = reviewRepository.countByRecipe(recipe);

                    return SimpleRecipeDto.builder()
                            .recipeUuid(recipe.getUuid())
                            .title(recipe.getRecipeTitle())
                            .recipeDescription(recipe.getRecipeDescription())
                            .thumbnail(recipe.getThumbnailUrl())
                            .writer(recipe.getCustomer().getNickName())
                            .recipeServings(recipe.getRecipeServings())
                            .recipeCookingTime(recipe.getRecipeCookingTime())
                            .likeCnt(likeCount)
                            .reviewCnt(reviewCount)
                            .recipeView(0L)
                            .build();
                })
                .toList();

        return new PageImpl<>(result, pageable, recipes.getTotalElements());


    }

    public Page<SimpleRecipeDto> testCount(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findRecipeVer5(pageable);

        List<Long> recipeIds = recipes.stream()
                .map(Recipe::getId)
                .toList();

        Map<Long, Long> likeCountMap = likeRepository.getLikeCountByRecipeIds(recipeIds);
        Map<Long, Long> reviewCountMap = reviewRepository.getReviewCountByRecipeIds(recipeIds);


        List<SimpleRecipeDto> result = recipes.stream()
                .map(recipe -> {
                    Long likeCount = likeCountMap.getOrDefault(recipe.getId(), 0L);
                    Long reviewCount = reviewCountMap.getOrDefault(recipe.getId(), 0L);
                    return SimpleRecipeDto.builder()
                            .recipeUuid(recipe.getUuid())
                            .title(recipe.getRecipeTitle())
                            .recipeDescription(recipe.getRecipeDescription())
                            .thumbnail(recipe.getThumbnailUrl())
                            .writer(recipe.getCustomer().getNickName())
                            .recipeServings(recipe.getRecipeServings())
                            .recipeCookingTime(recipe.getRecipeCookingTime())
                            .likeCnt(likeCount)
                            .reviewCnt(reviewCount)
                            .recipeView(0L)
                            .build();
                })
                .toList();

        return new PageImpl<>(result, pageable, recipes.getTotalElements());

    }


}
