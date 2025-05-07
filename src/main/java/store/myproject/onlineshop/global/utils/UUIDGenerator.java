package store.myproject.onlineshop.global.utils;


import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

public class UUIDGenerator {

    // UUID V7 생성
    public static UUID generateUUIDv7() {
        return UuidCreator.getTimeBased();
    }
}
