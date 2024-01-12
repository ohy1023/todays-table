package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.corporation.Corporation;
import store.myproject.onlineshop.domain.corporation.dto.CorporationJoinRequest;
import store.myproject.onlineshop.domain.corporation.dto.CorporationLoginRequest;
import store.myproject.onlineshop.domain.corporation.repository.CorporationRepository;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.redis.RedisDao;
import store.myproject.onlineshop.global.utils.JwtUtils;

import java.util.concurrent.TimeUnit;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CorporationService {

    private final CorporationRepository corporationRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisDao redisDao;
    private final JwtUtils jwtUtils;

    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;


    @Transactional
    public MessageResponse join(CorporationJoinRequest request) {

        corporationRepository.findByCompanyName(request.getCompanyName())
                .ifPresent(corporation -> {
                    throw new AppException(DUPLICATE_REGISTRATION_NUMBER, DUPLICATE_REGISTRATION_NUMBER.getMessage());
                });

        corporationRepository.findByCompanyEmail(request.getCompanyEmail())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_COMPANY_EMAIL, DUPLICATE_COMPANY_EMAIL.getMessage());
                });

        Corporation corporation = request.toEntity(encoder.encode(request.getPassword()));

        corporationRepository.save(corporation);

        return new MessageResponse("회원가입 성공");
    }


    @Transactional
    public LoginResponse login(CorporationLoginRequest request) {

        Corporation findCorporation = validateByRegistrationNumber(request.getRegistrationNumber());

        if (mismatchPassword(request.getPassword(), findCorporation.getPassword())) {
            throw new AppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());
        }

        String accessToken = jwtUtils.createAccessToken(request.getRegistrationNumber());
        String refreshToken = jwtUtils.createRefreshToken(request.getRegistrationNumber());

        if (accessToken == null || refreshToken == null) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        // 저장 형태 {"RT:123-45-678" , "refreshToken"}
        redisDao.setValues("RT:" + findCorporation.getRegistrationNumber(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse reissue(TokenRequest request, String companyEmail) {

        Corporation findCorporation = validateByRegistrationNumber(companyEmail);

        if (jwtUtils.isExpired(request.getRefreshToken())) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        String refreshToken = redisDao.getValues("RT:" + companyEmail);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(INVALID_REQUEST, INVALID_REQUEST.getMessage());
        }
        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        String newAccessToken = jwtUtils.createAccessToken(findCorporation.getCompanyEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(findCorporation.getCompanyEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCorporation.getCompanyEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new LoginResponse(newAccessToken, newRefreshToken);

    }

    @Transactional
    public MessageResponse logout(TokenRequest request, String registrationNumber) {

        Corporation findCorporation = validateByRegistrationNumber(registrationNumber);

        String accessToken = request.getAccessToken();

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        if (jwtUtils.isValid(accessToken)) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        // Token 삭제
        redisDao.deleteValues("RT:" + findCorporation.getRegistrationNumber());

        int expiration = jwtUtils.getExpiration(request.getAccessToken()).intValue() / 1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(request.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return new MessageResponse("로그아웃 되었습니다.");
    }

    private Corporation validateByRegistrationNumber(String registrationNumber) {
        return corporationRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new AppException(CORPORATION_NOT_FOUND, CORPORATION_NOT_FOUND.getMessage()));
    }


    private boolean mismatchPassword(String rawPassword, String encodedPassword) {
        return !encoder.matches(rawPassword, encodedPassword);
    }
}
