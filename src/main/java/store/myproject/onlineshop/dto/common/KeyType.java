package store.myproject.onlineshop.dto.common;

public enum KeyType {

    // Recipe
    RECIPE_DETAIL_CACHE("recipe:detail:%s"),

    // Recipe Lock
    RECIPE_DETAIL_CACHE_LOCK("recipe:detail:%s:lock"),

    // Item
    ITEM_DETAIL_CACHE("item:detail:%s"),

    ITEM_DETAIL_CACHE_LOCK("item:detail:%s:lock"),
    ;

    private final String keyPattern;

    KeyType(String keyPattern) {
        this.keyPattern = keyPattern;
    }

    public String format(Object... args) {
        return String.format(keyPattern, args);
    }
}