package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common (Authentication)
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "틀린 비밀번호입니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 접근은 금지되었습니다."),

    // Jwt 관련 에러 추가
    ACCESS_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Access Token이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Access Token입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Access Token입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Refresh Token이 없습니다."),
    MISMATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 Refresh Token입니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 Refresh Token입니다. 다시 로그인 해주세요."),


    // Customer
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원은 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),
    ALREADY_ADMIN(HttpStatus.CONFLICT, "이미 Admin 입니다."),
    MISMATCH_PASSWORD(HttpStatus.CONFLICT, "비밀번호가 틀렸습니다."),

    // Corporation
    DUPLICATE_REGISTRATION_NUMBER(HttpStatus.CONFLICT, "중복된 사업자 번호입니다."),
    DUPLICATE_COMPANY_EMAIL(HttpStatus.CONFLICT, "중복된 회사 이메일입니다."),
    CORPORATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회사입니다."),
    CORPORATION_NOT_ACCESS(HttpStatus.UNAUTHORIZED, "기업용 회원은 사용 불가한 기능입니다."),
    UNSUPPORTED_OBJECT_TPYE(HttpStatus.BAD_REQUEST, "지원하지 않는 객체입니다."),

    // Brand
    DUPLICATE_BRAND(HttpStatus.CONFLICT, "중복된 브랜드 이름입니다."),
    BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 브랜드 이름입니다."),

    // MemberShip
    DUPLICATE_MEMBERSHIP(HttpStatus.CONFLICT, "중복된 멤버십 이름입니다."),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 멤버쉽 이름입니다."),
    MEMBERSHIP_ACCESS_LIMIT(HttpStatus.CONFLICT, "더 이상 올라갈 멤버쉽이 존재하지 않습니다."),
    NOT_ENOUGH_MEMBERSHIP(HttpStatus.CONFLICT, "멤버쉽 업그레이드 자격이 부족합니다."),


    // Item
    DUPLICATE_ITEM(HttpStatus.CONFLICT, "중복된 품목 이름입니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 품목 이름입니다."),

    // Stock
    STOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "재고 정보가 입력되지 않았습니다."),
    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),

    // Account
    WITHDRAW_BAD_REQUEST(HttpStatus.BAD_REQUEST, "출금액이 보유자산을 초과합니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌가 등록되어 있지 않습니다. \n 계좌를 등록해주세요. "),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문 내역입니다."),
    ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문 상품 내역입니다."),
    DUPLICATE_MERCHANT_UID(HttpStatus.CONFLICT, "해당 merchant_uid가 이미 존재합니다."),
    FAILED_PREPARE_VALID(HttpStatus.CONFLICT, "사전 검증에 실패했습니다."),
    WRONG_PAYMENT_AMOUNT(HttpStatus.CONFLICT, "결제금액이 다릅니다."),
    ALREADY_ARRIVED(HttpStatus.CONFLICT, "배송 완료된 상품입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "결제 정보가 존재하지 않습니다."),

    // Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원은 장바구니에 아무것도 없습니다."),

    // CartItem
    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니에 해당 아이템이 없습니다."),
    CART_ITEM_NOT_EXIST_IN_CART(HttpStatus.NOT_FOUND, "장바구니가 비었습니다."),
    CHECK_NOT_EXIST_IN_CART(HttpStatus.NOT_FOUND, "구매할려는 품목을 체크해주세요"),

    // Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레시피가 존재하지 않습니다."),
    DUPLICATE_RECIPE(HttpStatus.CONFLICT, "해당 레시피 제목이 중복됩니다."),

    // Review
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),
    EMPTY_CONTENT(HttpStatus.NOT_FOUND, "댓글은 1자 이상 입력 필수입니다."),
    INVALID_REVIEW(HttpStatus.CONFLICT, "해당 레시피에 대한 댓글이 아닙니다."),

    // S3
    WRONG_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    ;

    private HttpStatus httpStatus;
    private String message;
}
