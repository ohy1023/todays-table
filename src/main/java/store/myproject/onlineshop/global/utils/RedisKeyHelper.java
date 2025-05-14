package store.myproject.onlineshop.global.utils;

import lombok.experimental.UtilityClass;
import store.myproject.onlineshop.domain.KeyType;

import java.util.UUID;

@UtilityClass
public class RedisKeyHelper {

    public String getRecipeKey(UUID recipeUuid) {
        return KeyType.RECIPE_DETAIL_CACHE.format(recipeUuid);
    }

    public String getRecipeLockKey(UUID recipeUuid) {
        return KeyType.RECIPE_DETAIL_CACHE_LOCK.format(recipeUuid);
    }
}