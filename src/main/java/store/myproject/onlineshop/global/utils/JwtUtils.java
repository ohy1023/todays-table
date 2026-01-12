package store.myproject.onlineshop.global.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final Long accessTokenExpiration;
    private final Long refreshTokenExpiration;

    public JwtUtils(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access.expiration}") Long accessTokenExpiration,
            @Value("${jwt.refresh.expiration}") Long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(String email) {
        return Jwts.builder()
                .claim("email", email) // .setClaims(claims) 대신 직접 넣거나 claims() 사용
                .issuedAt(new Date(System.currentTimeMillis())) // setIssuedAt -> issuedAt
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)) // setExpiration -> expiration
                .signWith(key) // 알고리즘은 키 길이에 따라 자동 선택됨 (HS256)
                .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
                .claim("email", email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    public boolean isExpired(String token) {
        try {
            return extractClaims(token)
                    .getExpiration()
                    .before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public boolean isInvalid(String token) {
        try {
            extractClaims(token);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public Long getExpiration(String token) {
        Date expiration = extractClaims(token).getExpiration();
        Long now = System.currentTimeMillis();
        return (expiration.getTime() - now);
    }

    public Claims extractClaims(String token) {
        // parserBuilder() -> parser()로 변경됨
        return Jwts.parser()
                .verifyWith(key) // setSigningKey -> verifyWith
                .build()
                .parseSignedClaims(token) // parseClaimsJws -> parseSignedClaims
                .getPayload(); // getBody -> getPayload
    }

    public String getEmail(String token) {
        return extractClaims(token).get("email", String.class);
    }
}