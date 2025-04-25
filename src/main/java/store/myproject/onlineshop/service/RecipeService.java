package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
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
import store.myproject.onlineshop.repository.review.ReviewRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.*;
import java.util.stream.Collectors;

import static store.myproject.onlineshop.exception.ErrorCode.*;

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

    /**
     * 단일 레시피 정보를 조회하고 조회수를 증가시킵니다.
     */
    public RecipeDto getRecipe(Long recipeId) {
        Recipe recipe = findRecipeById(recipeId);
        recipe.addViewCnt();
        return recipe.toDto();
    }

    /**
     * 레시피 요약정보를 조회합니다.
     */
    public Page<SimpleRecipeDto> getAllRecipe(Pageable pageable) {
        Page<Recipe> recipes = recipeRepository.findAll(pageable);

        List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
        Map<Long, Long> likeCounts = likeRepository.getLikeCountByRecipeIds(recipeIds);
        Map<Long, Long> reviewCounts = reviewRepository.getReviewCountByRecipeIds(recipeIds);

        return recipes.map(recipe -> {
            Long likeCnt = likeCounts.getOrDefault(recipe.getId(), 0L);
            Long reviewCnt = reviewCounts.getOrDefault(recipe.getId(), 0L);

            return SimpleRecipeDto.builder()
                    .recipeId(recipe.getId())
                    .title(recipe.getRecipeTitle())
                    .recipeDescription(recipe.getRecipeDescription())
                    .writer(recipe.getCustomer().getNickName())
                    .recipeCookingTime(recipe.getRecipeCookingTime())
                    .recipeServings(recipe.getRecipeServings())
                    .recipeView(recipe.getRecipeViewCnt())
                    .thumbnail(recipe.getThumbnailUrl())
                    .likeCnt(likeCnt)
                    .reviewCnt(reviewCnt)
                    .build();
        });
    }

    /**
     * 레시피를 작성하고 저장합니다. 이미지 파일과 레시피 재료도 함께 저장됩니다.
     */
    public MessageResponse createRecipe(RecipeCreateRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        Recipe recipe = request.toEntity(customer);

        List<RecipeItem> recipeItems = mapRecipeItems(request.getItemIdList());
        recipe.addItems(recipeItems);

        List<RecipeStep> steps = request.getSteps().stream()
                .map(stepReq -> RecipeStep.builder()
                        .stepOrder(stepReq.getOrder())
                        .content(stepReq.getContent())
                        .imageUrl(stepReq.getImageUrl())
                        .build())
                .toList();

        recipe.setStepList(steps);

        if (request.getThumbnailUrl() != null) {
            recipe.setThumbnailUrl(request.getThumbnailUrl());
        } else {
            steps.stream()
                    .map(RecipeStep::getImageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .findFirst()
                    .ifPresent(recipe::setThumbnailUrl);
        }

        recipeRepository.save(recipe);

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_ADDED));
    }


    /**
     * 기존 레시피를 수정합니다. 이미지 파일도 함께 업데이트됩니다.
     */
    public MessageResponse updateRecipe(Long recipeId, RecipeUpdateRequest request, String email) {
        Customer customer = findCustomerByEmail(email);
        Recipe recipe = findRecipeById(recipeId);

        if (!hasPermission(customer, recipe.getCustomer())) {
            throw new AppException(FORBIDDEN_ACCESS); // 권한 없음 예외 처리
        }

        // 1. 기본 정보 수정
        recipe.updateRecipe(request);

        // 2. 기존 재료 및 스텝 초기화
        recipe.getItemList().clear();     // orphanRemoval = true 로 자동 삭제
        recipe.getStepList().clear();     // orphanRemoval + cascade 로 자동 삭제

        // 3. 새로운 RecipeItem 설정
        List<RecipeItem> recipeItems = mapRecipeItems(request.getItemIdList());
        recipe.addItems(recipeItems);  // 편의 메서드로 연관관계 설정

        // 4. 새로운 RecipeStep 설정
        List<RecipeStep> steps = request.getSteps().stream()
                .map(stepReq -> RecipeStep.builder()
                        .stepOrder(stepReq.getOrder())
                        .content(stepReq.getContent())
                        .imageUrl(stepReq.getImageUrl())
                        .build())
                .toList();
        recipe.setStepList(steps);  // 단방향이므로 리스트만 세팅

        if (request.getThumbnailUrl() != null) {
            recipe.setThumbnailUrl(request.getThumbnailUrl());
        } else {
            steps.stream()
                    .map(RecipeStep::getImageUrl)
                    .filter(url -> url != null && !url.isBlank())
                    .findFirst()
                    .ifPresent(recipe::setThumbnailUrl);
        }

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_MODIFIED));
    }

    /**
     * 레시피를 삭제합니다. 작성자 또는 관리자만 가능
     */
    public MessageResponse deleteRecipe(Long recipeId, String email) {
        Customer customer = findCustomerByEmail(email);
        Recipe recipe = findRecipeById(recipeId);

        if (!hasPermission(customer, recipe.getCustomer())) {
            throw new AppException(FORBIDDEN_ACCESS); // 권한 없음 예외 처리
        }

        recipeRepository.deleteById(recipe.getId());

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_DELETED));
    }

    /**
     * 해당 레시피의 댓글과 대댓글 미리보기(3개)를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByRecipe(Long recipeId, Pageable pageable) {
        Recipe recipe = findRecipeById(recipeId);

        Page<Review> parentReviews = reviewRepository.findParentReviews(recipe.getId(), pageable); // 댓글

        List<Long> parentReviewIds = parentReviews.stream().map(Review::getId).toList();

        Map<Long, List<Review>> childMap = reviewRepository.findTop3ChildReviews(parentReviewIds, PageRequest.of(0, 3)).stream()
                .collect(Collectors.groupingBy(Review::getParentId));

        // 대댓글 개수 조회
        Map<Long, Long> childCountMap = reviewRepository.countByParentIds(parentReviewIds);

        return parentReviews.map(parentReview -> {
            List<ChildReviewResponse> childReviewResponses = Optional.ofNullable(childMap.get(parentReview.getId()))
                    .orElse(List.of())
                    .stream()
                    .map(r -> ChildReviewResponse.builder()
                            .id(r.getId())
                            .writer(r.getCustomer().getNickName())
                            .content(r.getReviewContent())
                            .build())
                    .toList();

            long totalChildCount = childCountMap.getOrDefault(parentReview.getId(), 0L);
            boolean hasMoreChild = totalChildCount > 3;

            return ReviewResponse.builder()
                    .id(parentReview.getId())
                    .writer(parentReview.getCustomer().getNickName())
                    .content(parentReview.getReviewContent())
                    .childReviews(childReviewResponses)
                    .hasMoreChildReviews(hasMoreChild)
                    .build();
        });

    }

    /**
     * 대댓글 더보기.
     */
    @Transactional(readOnly = true)
    public Page<ChildReviewResponse> getChildReviews(Long recipeId, Long parentReviewId, Pageable pageable) {
        // 레시피 및 부모 댓글 존재 검증
        findRecipeById(recipeId);
        Review parentReview = findReviewById(parentReviewId);

        if (!parentReview.getRecipe().getId().equals(recipeId)) {
            throw new AppException(INVALID_REVIEW);
        }

        return reviewRepository.findByParentId(parentReviewId, pageable)
                .map(review -> ChildReviewResponse.builder()
                        .id(review.getId())
                        .writer(review.getCustomer().getNickName())
                        .content(review.getReviewContent())
                        .build());
    }

    /**
     * 레시피에 댓글 또는 대댓글을 작성합니다.
     */
    public MessageResponse addReview(String email, Long recipeId, ReviewWriteRequest request) {
        Customer customer = findCustomerByEmail(email);
        Recipe recipe = findRecipeById(recipeId);

        Review review = (request.getReviewParentId() == null) ?
                request.toEntity(0L, request.getReviewContent(), customer, recipe) :
                request.toEntity(request.getReviewParentId(), request.getReviewContent(), customer, recipe);

        review.addReviewToRecipe(recipe);
        reviewRepository.save(review);

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_REVIEW_ADDED));
    }

    /**
     * 댓글 또는 대댓글을 수정합니다. 작성자 또는 관리자만 가능
     */
    public MessageResponse updateReview(String email, Long recipeId, Long reviewId, ReviewUpdateRequest request) {
        Customer customer = findCustomerByEmail(email);
        findRecipeById(recipeId);
        Review review = findReviewById(reviewId);

        if (hasPermission(customer, review.getCustomer())) {
            review.updateReview(request);
        } else {
            throw new AppException(FORBIDDEN_ACCESS, FORBIDDEN_ACCESS.getMessage());
        }

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_REVIEW_MODIFIED));
    }

    /**
     * 댓글 또는 대댓글을 삭제합니다. 작성자 또는 관리자만 가능
     */
    public MessageResponse deleteReview(String email, Long recipeId, Long reviewId) {
        Customer customer = findCustomerByEmail(email);
        findRecipeById(recipeId);
        Review review = findReviewById(reviewId);

        if (hasPermission(customer, review.getCustomer())) {
            review.removeReviewToRecipe();
            reviewRepository.delete(review);
        } else {
            throw new AppException(FORBIDDEN_ACCESS, FORBIDDEN_ACCESS.getMessage());
        }

        return new MessageResponse(messageUtil.get(MessageCode.RECIPE_REVIEW_DELETED));
    }

    /**
     * 레시피 좋아요를 토글합니다. 이미 좋아요가 있으면 취소
     */
    public MessageResponse toggleLike(Long recipeId, String email) {
        Customer customer = findCustomerByEmail(email);
        Recipe recipe = findRecipeById(recipeId);

        Optional<Like> like = likeRepository.findByRecipeAndCustomer(recipe, customer);

        if (like.isPresent()) {
            likeRepository.delete(like.get());
            return new MessageResponse(messageUtil.get(MessageCode.UNDO_LIKE));
        } else {
            likeRepository.save(Like.of(customer, recipe));
            return new MessageResponse(messageUtil.get(MessageCode.DO_LIKE));
        }
    }

    /**
     * 해당 레시피에 대한 좋아요 수를 반환합니다.
     */
    public Long getLikeCount(Long recipeId) {
        Recipe recipe = findRecipeById(recipeId);
        return likeRepository.countByRecipe(recipe);
    }

    /**
     * 레시피 단계 이미지 업로드
     */
    public MessageResponse uploadImage(MultipartFile file) {
        return new MessageResponse(awsS3Service.uploadRecipeOriginImage(file));
    }

    /**
     * 아이템 ID 리스트를 기반으로 RecipeItem 리스트를 생성합니다.
     */
    private List<RecipeItem> mapRecipeItems(List<Long> itemIds) {
        List<RecipeItem> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            Item item = findItemById(itemId);
            items.add(RecipeItem.createRecipeItem(item));
        }
        return items;
    }

    /**
     * 이메일로 고객을 조회합니다.
     */
    private Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));
    }

    /**
     * 아이디로 품목을 조회합니다.
     */
    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
    }

    /**
     * 아이디로 레시피를 조회합니다.
     */
    private Recipe findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND, RECIPE_NOT_FOUND.getMessage()));
    }

    /**
     * 아이디로 리뷰를 조회합니다.
     */
    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(REVIEW_NOT_FOUND, REVIEW_NOT_FOUND.getMessage()));
    }

    /**
     * 요청자가 관리자이거나 작성자인지 확인합니다.
     */
    private boolean hasPermission(Customer requester, Customer target) {
        return requester.getCustomerRole() == CustomerRole.ROLE_ADMIN || requester == target;
    }
}
