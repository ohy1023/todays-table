package store.myproject.onlineshop.domain;

public enum MessageCode {
    // 브랜드 관련
    BRAND_ADDED("brand.added"),
    BRAND_MODIFIED("brand.modified"),
    BRAND_DELETED("brand.deleted"),

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
