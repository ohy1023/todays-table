package store.myproject.onlineshop.global.utils;

import lombok.experimental.UtilityClass;
import store.myproject.onlineshop.dto.common.KeyType;

import java.util.UUID;

@UtilityClass
public class RedisKeyHelper {

    public String getRecipeKey(UUID recipeUuid) {
        return KeyType.RECIPE_DETAIL_CACHE.format(recipeUuid);
    }

    public String getRecipeLockKey(UUID recipeUuid) {
        return KeyType.RECIPE_DETAIL_CACHE_LOCK.format(recipeUuid);
    }

    public String getItemCacheKey(UUID itemUuid) {
        return KeyType.ITEM_DETAIL_CACHE.format(itemUuid);
    }

    public String getItemLockKey(UUID itemUuid) {
        return KeyType.ITEM_DETAIL_CACHE_LOCK.format(itemUuid);
    }
}