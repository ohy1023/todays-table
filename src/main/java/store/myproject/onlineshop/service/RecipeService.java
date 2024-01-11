package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.imagefile.repository.ImageFileRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.like.repository.LikeRepository;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateResponse;
import store.myproject.onlineshop.domain.recipe.repository.RecipeRepository;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteResponse;
import store.myproject.onlineshop.domain.review.repository.ReviewRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.s3.service.AwsS3Service;
import store.myproject.onlineshop.global.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private final ImageFileRepository imageFileRepository;
    private final AwsS3Service awsS3Service;


    /**
     * 사용자가 작성한 레시피를 저장하고, 레시피에 속한 재료들을 연결하는 메서드입니다.
     *
     * @param email               사용자 이메일
     * @param recipeCreateRequest 사용자가 작성한 레시피 정보
     * @param multipartFileList   업로드된 이미지 파일 리스트
     * @return RecipeCreateResponse 작성된 레시피의 응답 정보
     */
    public RecipeCreateResponse writeRecipe(String email, RecipeCreateRequest recipeCreateRequest, List<MultipartFile> multipartFileList) {
        // 현재 로그인한 회원을 검증합니다.
        Customer customer = validateByEmail(email);

        // 레시피 중복을 검사합니다.
        checkRecipeTitle(recipeCreateRequest);

        // Recipe 엔티티를 생성하고 저장합니다.
        Recipe saveRecipe = recipeRepository.save(recipeCreateRequest.toEntity(customer));

        // 업로드된 이미지 파일이 존재하는 경우 처리합니다.
        if (multipartFileList != null) {
            for (MultipartFile multipartFile : multipartFileList) {
                String originImageUrl = awsS3Service.uploadRecipeOriginImage(multipartFile);

                // 이미지 파일 엔티티를 생성하고 저장합니다.
                ImageFile image = ImageFile.createImage(originImageUrl, saveRecipe);

                // 연관관계 정의
                image.addRecipe(saveRecipe);

                imageFileRepository.save(image);
            }
        }

        // RecipeItem 리스트를 생성합니다.
        List<RecipeItem> recipeItemList = createRecipeItems(recipeCreateRequest.getItemIdList());

        // 생성된 RecipeItem들을 Recipe와 연관시키고 저장합니다.
        for (RecipeItem recipeItem : recipeItemList) {
            recipeItem.setRecipeAndItem(saveRecipe, recipeItem.getItem());
        }

        // 저장된 Recipe를 기반으로 응답을 생성하여 반환합니다.
        return saveRecipe.fromEntity(saveRecipe);
    }

    /**
     * 주어진 레시피 제목이 이미 존재하는지 검증하고, 중복된 경우 예외를 발생시키는 메서드입니다.
     *
     * @param recipeCreateRequest 레시피 생성 요청 정보
     * @throws AppException 레시피 제목이 이미 존재하는 경우 발생하는 예외
     */
    private void checkRecipeTitle(RecipeCreateRequest recipeCreateRequest) {
        recipeRepository.findByRecipeTitle(recipeCreateRequest.getRecipeTitle())
                .ifPresent(recipe -> {
                    throw new AppException(DUPLICATE_RECIPE, DUPLICATE_RECIPE.getMessage());
                });
    }


    /**
     * 특정 레시피에 댓글,대댓글 작성 메서드입니다.
     *
     * @param recipeId           레시피 ID
     * @param email              사용자 이메일
     * @param reviewWriteRequest 대댓글 여부, 댓글 내용
     * @return ReviewWriteResponse 댓글 종류, 댓글 내용, 작성자 이메일, 레세피 ID
     */
    public ReviewWriteResponse writeReview(String email, Long recipeId, ReviewWriteRequest reviewWriteRequest) {
        Customer customer = validateByEmail(email);

        Recipe recipe = validateByRecipe(recipeId);

        Review review;
        if (reviewWriteRequest.getReviewParentId() == null) {
            // 댓글
            review = reviewWriteRequest.toEntity(0L, reviewWriteRequest.getReviewContent(), customer, recipe);
        } else {
            // 대댓글
            review = reviewWriteRequest.toEntity(reviewWriteRequest.getReviewParentId(), reviewWriteRequest.getReviewContent(), customer, recipe);
        }

        // 레시피에 리뷰 추가
        review.addReviewToRecipe(recipe);

        // 리뷰 저장
        Review saveReview = reviewRepository.save(review);

        return saveReview.toWriteResponse();
    }

    /**
     * @param email               사용자 이메일
     * @param recipeId            레시피 ID
     * @param reviewId            댓글 ID
     * @param reviewUpdateRequest 수정할 댓글 내용
     * @return ReviewUpdateResponse 댓글 종류, 댓글 내용, 작성자 이메일, 레세피 ID
     */
    public ReviewUpdateResponse updateReview(String email, Long recipeId, Long reviewId, ReviewUpdateRequest reviewUpdateRequest) {
        // 이메일을 통해 현재 로그인한 회원을 검증합니다.
        Customer customer = validateByEmail(email);

        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 댓글 ID를 통해 현재 조회하고자 하는 댓글을 검증합니다.
        Review review = validateByReview(reviewId);

        // (로그인 한 회원 or 관리자)과 댓글 수정을 요청한 회원이 동일한지 검증합니다.
        if (checkPermission(customer, review)) {
            review.updateReview(reviewUpdateRequest);

        } else {
            // 동일하지 않으면 권한 오류 발생
            throw new AppException(FORBIDDEN_ACCESS, FORBIDDEN_ACCESS.getMessage());
        }

        return review.toUpdateResponse();
    }


    /**
     * 특정 레시피에 댓글,대댓글 삭제 메서드입니다.
     *
     * @param recipeId 레시피 ID
     * @param email    사용자 이메일
     * @param reviewId 댓글 ID
     * @return MessageResponse
     */
    public MessageResponse deleteReview(String email, Long recipeId, Long reviewId) {
        // 이메일을 통해 현재 로그인한 회원을 검증합니다.
        Customer customer = validateByEmail(email);

        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 댓글 ID를 통해 현재 조회하고자 하는 댓글을 검증합니다.
        Review review = validateByReview(reviewId);

        // (로그인 한 회원 or 관리자)과 댓글 삭제를 요청한 회원이 동일한지 검증합니다.
        if (checkPermission(customer, review)) {
            // 레시피에 리뷰 삭제
            review.removeReviewToRecipe();

            // 댓글 삭제
            reviewRepository.delete(review);

        } else {
            // 동일하지 않으면 권한 오류 발생
            throw new AppException(FORBIDDEN_ACCESS, FORBIDDEN_ACCESS.getMessage());
        }


        return new MessageResponse("댓글이 성공적으로 삭제되었습니다.");
    }

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
     * 주어진 itemIdList를 사용하여 RecipeItem 리스트를 생성합니다.
     *
     * @param itemIdList Item의 ID 리스트
     * @return 생성된 RecipeItem 리스트
     * @throws AppException 주어진 itemId로 조회한 Item이 존재하지 않을 경우 발생
     */
    private List<RecipeItem> createRecipeItems(List<Long> itemIdList) {
        List<RecipeItem> recipeItemList = new ArrayList<>();

        for (Long itemId : itemIdList) {
            Item item = validateByItemId(itemId);

            recipeItemList.add(RecipeItem.createRecipeItem(item));
        }

        return recipeItemList;
    }

    /**
     * 주어진 itemId로 Item을 조회하고, 존재하지 않을 경우 예외를 발생시킵니다.
     *
     * @param itemId Item의 ID
     * @return 조회된 Item
     * @throws AppException 주어진 itemId로 조회한 Item이 존재하지 않을 경우 발생
     */
    private Item validateByItemId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
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
                .orElseThrow(() -> new AppException(RECIPE_NOT_FOUND, RECIPE_NOT_FOUND.getMessage()));
    }

    /**
     * 댓글 ID를 이용하여 현재 조회하고자 하는 댓글이 존재하는지 검증하는 메서드입니다.
     *
     * @param id 댓글 ID
     * @return Review 객체
     * @throws AppException 해당 ID에 대한 댓글이 존재하지 않을 경우 발생
     */
    private Review validateByReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new AppException(REVIEW_NOT_FOUND, REVIEW_NOT_FOUND.getMessage()));
    }


    /**
     * 댓글 삭제 요청자의 권한 체크하는 메서드입니다.
     *
     * @param customer Customer (요청자)
     * @param review   Review (댓글 작성자)
     * @return 권한 여부
     */
    private boolean checkPermission(Customer customer, Review review) {
        // 관리자 권한 통과합니다.
        if (customer.getCustomerRole() == CustomerRole.ROLE_ADMIN) {
            return true;
        }

        // 동일성 검증합니다.
        return customer == review.getCustomer();
    }

}
