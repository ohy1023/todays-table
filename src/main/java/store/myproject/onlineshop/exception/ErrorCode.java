package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common (Authentication)
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "틀린 비밀번호입니다."),
    INVALID_REQUEST(HttpStatus.UNAUTHORIZED, "잘못된 응답입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "해당 토큰을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "해당 접근은 금지되었습니다."),

    // Customer
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원은 존재하지 않습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 이메일이 존재하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 이메일입니다."),

    ALREADY_ADMIN(HttpStatus.CONFLICT, "이미 Admin 입니다."),

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

    NOT_ENOUGH_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),

    // Account
    WITHDRAW_BAD_REQUEST(HttpStatus.BAD_REQUEST, "출금액이 보유자산을 초과합니다."),

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌가 등록되어 있지 않습니다. \n 계좌를 등록해주세요. "),

    // Order
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 주문 내역입니다."),

    NOT_ENOUGH_MONEY(HttpStatus.CONFLICT, "보유금액이 부족합니다."),

    // Cart
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원은 장바구니에 아무것도 없습니다."),

    // S3
    WRONG_FILE_FORMAT(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    ;

    private HttpStatus httpStatus;
    private String message;
}
