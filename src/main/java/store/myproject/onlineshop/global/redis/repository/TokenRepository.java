package store.myproject.onlineshop.global.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.global.redis.entity.TokenEntity;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<TokenEntity, String> {

    Optional<TokenEntity> findByAccessToken(String accessToken);
    Optional<TokenEntity> findByRefreshToken(String refreshToken);
    Optional<TokenEntity> findByUserId(String userId);
    Optional<TokenEntity> findByAccessTokenAndRefreshToken(String accessToken, String refreshToken);

}
