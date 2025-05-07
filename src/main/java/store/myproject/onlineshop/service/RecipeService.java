package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;
import store.myproject.onlineshop.domain.review.dto.*;
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
import store.myproject.onlineshop.repository.recipestep.RecipeStepRepository;
import store.myproject.onlineshop.repository.review.ReviewRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static store.myproject.onlineshop.exception.ErrorCode.CUSTOMER_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.FORBIDDEN_ACCESS;
import static store.myproject.onlineshop.exception.ErrorCode.INVALID_REVIEW;
import static store.myproject.onlineshop.exception.ErrorCode.ITEM_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.RECIPE_NOT_FOUND;
import static store.myproject.onlineshop.exception.ErrorCode.REVIEW_NOT_FOUND;

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

    /**
     * 단일 레시피 정보를 조회하고 조회수를 증가시킵니다.
     */
    @Transactional(readOnly = true)
    public RecipeDto getRecipeDetail(UUID recipeUuid) {
        RecipeDto recipeDto = recipeRepository.findRecipeDtoByUuid(recipeUuid)
                .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND));

        List<RecipeStepDto> stepDtos = recipeStepRepository.findStepsByRecipeUuid(recipeUuid);
        List<RecipeItemDto> itemDtos = recipeItemRepository.findItemsByRecipeUuid(recipeUuid);

        recipeDto.setSteps(stepDtos);
        recipeDto.setItems(itemDtos);

//        recipeMetaService.asyncIncreaseViewCnt(recipeDto.getRecipeMetaUuid());

        return recipeDto;
    }

    /**
     * 레시피 요약 정보를 페이지 단위로 조회합니다.
     */
    @Transactional(readOnly = true)
    public Slice<SimpleRecipeDto> getRecipes(Pageable pageable) {
        return recipeRepository.findAllSimpleRecipes(pageable);
    }

    /**
     * 레시피를 등록합니다. 재료 및 단계, 썸네일까지 포함됩니다.
     */
    public MessageResponse createRecipe(RecipeCreateRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = request.toEntity(customer);
        recipe.addItems(mapToRecipeItems(request.getItemIdList()));
        recipe.addSteps(mapToRecipeSteps(request.getSteps()));
        applyThumbnail(recipe, request.getThumbnailUrl());
        recipeRepository.save(recipe);
        return new MessageResponse(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_ADDED));
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
        recipe.addItems(mapToRecipeItems(request.getItemIdList()));
        recipe.addSteps(mapToRecipeSteps(request.getSteps()));
        applyThumbnail(recipe, request.getThumbnailUrl());
        return new MessageResponse(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_MODIFIED));
    }

    /**
     * 레시피를 삭제합니다. 관리자 또는 작성자만 삭제할 수 있습니다.
     */
    public MessageResponse deleteRecipe(UUID recipeUuid, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeByUuid(recipeUuid);
        validatePermission(customer, recipe.getCustomer());
        recipeRepository.delete(recipe);
        return new MessageResponse(recipe.getUuid(), messageUtil.get(MessageCode.RECIPE_DELETED));
    }

    /**
     * 해당 레시피에 작성된 댓글과 대댓글 일부를 조회합니다.
     */
    public Page<ReviewResponse> getRecipeReviews(UUID recipeUuid, Pageable pageable) {
        Recipe recipe = getRecipeByUuid(recipeUuid);
        Page<Review> parents = reviewRepository.findParentReviews(recipe.getId(), pageable);
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
        if (!parent.getRecipe().getId().equals(recipeUuid)) {
            throw new AppException(INVALID_REVIEW);
        }
        return reviewRepository.findByParentId(parent.getParentId(), pageable)
                .map(this::toChildReviewDto);
    }

    /**
     * 댓글 또는 대댓글을 작성합니다.
     */
    public MessageResponse createReview(String email, UUID recipeUuid, ReviewWriteRequest request) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeWithMeta(recipeUuid);
        Long parentId = Optional.ofNullable(request.getReviewParentId()).orElse(0L);
        Review review = request.toEntity(parentId, request.getReviewContent(), customer, recipe);
        review.addReviewToRecipe(recipe);
        reviewRepository.save(review);
        recipeMetaService.asyncIncreaseReviewCnt(recipe.getRecipeMeta().getId());
        return new MessageResponse(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_ADDED));
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
        return new MessageResponse(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_MODIFIED));
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
        recipeMetaService.asyncDecreaseReviewCnt(recipe.getRecipeMeta().getId());
        return new MessageResponse(review.getUuid(), messageUtil.get(MessageCode.RECIPE_REVIEW_DELETED));
    }

    /**
     * 좋아요 토글 처리합니다. 이미 눌렀으면 삭제, 아니면 추가.
     */
    public MessageResponse toggleLike(UUID recipeUuid, String email) {
        Customer customer = getCustomerByEmail(email);
        Recipe recipe = getRecipeWithMeta(recipeUuid);
        Optional<Like> like = likeRepository.findByRecipeAndCustomer(recipe, customer);
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            recipeMetaService.asyncDecreaseLikeCnt(recipe.getRecipeMeta().getId());
            return new MessageResponse(messageUtil.get(MessageCode.UNDO_LIKE));
        }
        likeRepository.save(Like.of(customer, recipe));
        recipeMetaService.asyncIncreaseLikeCnt(recipe.getRecipeMeta().getId());
        return new MessageResponse(messageUtil.get(MessageCode.DO_LIKE));
    }

    /**
     * 레시피 단계 이미지 업로드
     */
    public MessageResponse uploadImage(MultipartFile file) {
        return new MessageResponse(awsS3Service.uploadRecipeOriginImage(file));
    }

    /**
     * 특정 아이템을 사용하는 레시피 목록 조회
     */
    public Page<SimpleRecipeDto> getRecipesByItem(UUID itemUuid, Pageable pageable) {
        return recipeRepository.findRecipeUseItem(itemUuid, pageable);
    }

    /**
     * 아이템 ID 리스트로 RecipeItem 리스트 생성
     */
    private List<RecipeItem> mapToRecipeItems(List<Long> itemIds) {
        return itemIds.stream()
                .map(this::getItemById)
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
                .id(parent.getId())
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
                .id(review.getId())
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
    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
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
}
