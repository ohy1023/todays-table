package store.myproject.onlineshop.domain;

public enum MessageCode {
    // 회원 관련
    CUSTOMER_JOIN("customer.join"),
    CUSTOMER_LOGOUT("customer.logout"),
    CUSTOMER_MODIFIED("customer.modified"),
    CUSTOMER_DELETED("customer.deleted"),
    CUSTOMER_PASSWORD_MODIFIED("customer.password.modified"),
    CUSTOMER_ROLE_MODIFIED("customer.role.modified"),
    EMAIL_AVAILABLE("customer.email.available"),
    NICKNAME_AVAILABLE("customer.nickname.available"),

    // 브랜드 관련
    BRAND_ADDED("brand.added"),
    BRAND_MODIFIED("brand.modified"),
    BRAND_DELETED("brand.deleted"),

    // 멤버쉽 관련
    MEMBERSHIP_ADDED("membership.added"),
    MEMBERSHIP_MODIFIED("membership.modified"),
    MEMBERSHIP_DELETED("membership.deleted"),

    // 상품 관련
    ITEM_MODIFIED("item.modified"),
    ITEM_DELETED("item.deleted"),

    // 레시피 관련
    RECIPE_ADDED("recipe.added"),
    RECIPE_MODIFIED("recipe.modified"),
    RECIPE_DELETED("recipe.deleted"),
    RECIPE_REVIEW_ADDED("recipe.review.added"),
    RECIPE_REVIEW_MODIFIED("recipe.review.modified"),
    RECIPE_REVIEW_DELETED("recipe.review.deleted"),

    // 좋아요 관련
    DO_LIKE("do.like"),
    UNDO_LIKE("undo.like"),

    // 주문 관련
    ORDER_DELIVERY_MODIFIED("order.delivery.modified"),
    ORDER_CANCEL("order.cancel"),
    ORDER_POST_VERIFICATION("order.post.verification"),

    // 장바구니 관련
    CART_ITEM_ADDED("cart.item.added"),
    CART_ITEM_DELETED("cart.item.deleted"),
    CART_CLEARED("cart.cleared"),
    CART_ITEM_CHECKED("cart.item.checked");

    private final String key;

    MessageCode(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
