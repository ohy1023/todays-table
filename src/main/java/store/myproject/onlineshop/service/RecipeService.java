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
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
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
     * @param info                사용자 정보
     * @param recipeCreateRequest 사용자가 작성한 레시피 정보
     * @param multipartFileList   업로드된 이미지 파일 리스트
     * @return RecipeCreateResponse 작성된 레시피의 응답 정보
     */
    public RecipeCreateResponse writeRecipe(RecipeCreateRequest recipeCreateRequest, List<MultipartFile> multipartFileList, String info) {
        // 현재 로그인한 회원을 검증합니다.
        Customer customer = validateCustomer(info);

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
     * 특정 레시피의 정보를 조회하는 메서드입니다.
     *
     * @param recipeId 조회할 레시피의 식별자
     * @return RecipeDto 조회된 레시피 정보를 담은 DTO (Data Transfer Object)
     */
    public RecipeDto viewOneRecipe(Long recipeId) {
        // 조회할 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 해당 레시피의 조회수를 증가시킵니다.
        recipe.updateView();

        // 하고 해당 레시피의 정보를 RecipeDto로 변환하여 반환합니다.
        return recipe.toDto();
    }

    // todo 레시피 전체 조회

    // todo 레시피 이름 검색

    /**
     * 특정 레시피를 업데이트하고 업데이트된 레시피 정보를 반환하는 메서드입니다.
     *
     * @param recipeId          업데이트할 레시피의 식별자
     * @param request           업데이트할 레시피 정보를 담은 요청 객체
     * @param multipartFileList 업로드된 이미지 파일 리스트
     * @param email             현재 로그인한 사용자의 이메일
     * @return RecipeDto         업데이트된 레시피 정보를 담은 DTO (Data Transfer Object)
     */
    public RecipeDto updateRecipe(Long recipeId, RecipeUpdateRequest request, List<MultipartFile> multipartFileList, String email) {
        // 현재 로그인한 사용자를 검증합니다.
        Customer customer = validateCustomer(email);

        // 업데이트할 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 현재 로그인한 사용자가 레시피의 작성자이거나 관리자 권한이 있는 경우에만 업데이트를 수행합니다.
        if (checkPermission(customer, recipe.getCustomer())) {
            // 업로드된 이미지 파일이 존재하는 경우 처리합니다.
            imageFileRepository.findAllByRecipe(recipe);

            if (multipartFileList != null) {
                for (MultipartFile multipartFile : multipartFileList) {
                    for (ImageFile imageFile : recipe.getImageFileList()) {

                        String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());
                        // 연관관계 제거
                        imageFile.removeRecipe(recipe);

                        awsS3Service.deleteBrandImage(extractFileName);
                    }

                    String newUrl = awsS3Service.uploadRecipeOriginImage(multipartFile);

                    ImageFile image = ImageFile.createImage(newUrl, recipe);

                    image.addRecipe(recipe);
                }
            }

            // 레시피를 업데이트합니다.
            recipe.updateRecipe(request);
        }

        // 업데이트된 레시피 정보를 DTO로 변환하여 반환합니다.
        return recipe.toDto();
    }


    /**
     * 사용자가 작성한 레시피를 삭제하는 메서드입니다.
     *
     * @param recipeId 삭제할 레시피의 식별자
     * @param email    현재 로그인한 사용자의 이메일
     * @return MessageResponse 삭제 결과를 나타내는 응답 메시지
     */
    public MessageResponse deleteRecipe(Long recipeId, String email) {
        // 현재 로그인한 사용자를 검증합니다.
        Customer customer = validateCustomer(email);

        // 삭제할 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 현재 로그인한 사용자가 레시피의 작성자이거나 관리자 권한이 있는 경우에만 삭제를 수행합니다.
        if (checkPermission(customer, recipe.getCustomer())) {
            recipeRepository.deleteById(recipe.getId());
        }

        return new MessageResponse("해당 레시피가 삭제되었습니다.");
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
        Customer customer = validateCustomer(email);

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
        Customer customer = validateCustomer(email);

        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 댓글 ID를 통해 현재 조회하고자 하는 댓글을 검증합니다.
        Review review = validateByReview(reviewId);

        // (로그인 한 회원 or 관리자)과 댓글 수정을 요청한 회원이 동일한지 검증합니다.
        if (checkPermission(customer, review.getCustomer())) {
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
        Customer customer = validateCustomer(email);

        // 레시피 ID를 통해 현재 조회하고자 하는 레시피를 검증합니다.
        Recipe recipe = validateByRecipe(recipeId);

        // 댓글 ID를 통해 현재 조회하고자 하는 댓글을 검증합니다.
        Review review = validateByReview(reviewId);

        // (로그인 한 회원 or 관리자)과 댓글 삭제를 요청한 회원이 동일한지 검증합니다.
        if (checkPermission(customer, review.getCustomer())) {
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
        Customer customer = validateCustomer(email);

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
    private Customer validateCustomer(String email) {
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
     * 권한 검사를 수행하여 요청자와 대상이 동일한지 확인합니다.
     * 관리자 권한을 가진 요청자는 항상 통과합니다.
     *
     * @param requester 요청자(Customer 객체)
     * @param target    대상(Customer 객체)
     * @return 동일한 사용자인 경우 true, 아닌 경우 false
     */
    private boolean checkPermission(Customer requester, Customer target) {
        // 관리자 권한을 가진 경우 항상 통과합니다.
        if (requester.getCustomerRole() == CustomerRole.ROLE_ADMIN) {
            return true;
        }

        // 동일성 검증합니다.
        return requester == target;
    }

}
