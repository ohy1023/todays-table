package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.annotation.SendMail;
import store.myproject.onlineshop.global.redis.RedisDao;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static store.myproject.onlineshop.exception.ErrorCode.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisDao redisDao;
    private final JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    public String secretKey;
    @Value("${jwt.access.expiration}")
    public Long accessTokenExpiration;
    @Value("${jwt.refresh.expiration}")
    public Long refreshTokenExpiration;
    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;


    @Transactional
    public String join(CustomerJoinRequest request) {
        log.info("회원가입 요청 : {}", request);

        customerRepository.findByNickName(request.getNickName())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage());
                });

        customerRepository.findByEmail(request.getEmail())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage());
                });

        Customer customer = request.toEntity(encoder.encode(request.getPassword()));
        Customer savedCustomer = customerRepository.save(customer);

        CustomerInfoResponse customerInfoResponse = CustomerInfoResponse.toDto(savedCustomer);
        log.info("회원가입 완료!");
        log.info("email: {}, nickName: {}, userName: {}", savedCustomer.getEmail(), savedCustomer.getNickName(), savedCustomer.getUserName());

        return customerInfoResponse.getEmail();
    }

    @Transactional
    public CustomerLoginResponse login(CustomerLoginRequest request) {

        Customer findCustomer = findCustomerByEmail(request.getEmail());

        if (!encoder.matches(request.getPassword(), findCustomer.getPassword())) {
            throw new AppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());
        }

        String accessToken = jwtUtils.createAccessToken(request.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(request.getEmail());

        if (accessToken == null || refreshToken == null) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCustomer.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new CustomerLoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public CustomerLoginResponse reissue(CustomerTokenRequest request, String email) {

        Customer findCustomer = findCustomerByEmail(email);

        if (jwtUtils.isExpired(request.getRefreshToken())) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        String refreshToken = redisDao.getValues("RT:" + email);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(INVALID_REQUEST, INVALID_REQUEST.getMessage());
        }
        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        String newAccessToken = jwtUtils.createAccessToken(findCustomer.getEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(findCustomer.getEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCustomer.getEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new CustomerLoginResponse(newAccessToken, newRefreshToken);

    }

    @Transactional
    public String logout(CustomerTokenRequest request, String email) {

        Customer findCustomer = findCustomerByEmail(email);

        String accessToken = request.getAccessToken();

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        if (jwtUtils.isValid(accessToken)) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        // Token 삭제
        redisDao.deleteValues("RT:" + findCustomer.getEmail());

        int expiration = jwtUtils.getExpiration(request.getAccessToken()).intValue() / 1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(request.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return "로그아웃 되었습니다.";
    }

    @Transactional
    public String emailCheck(CustomerEmailCheckRequest request) {

        customerRepository.findByEmail(request.getEmail())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage());
                });

        return "사용 가능한 이메일 입니다.";
    }

    @Transactional
    public String nickNameCheck(CustomerNickNameCheckRequest request) {

        customerRepository.findByNickName(request.getNickName())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage());
                });

        return "사용 가능한 닉네임 입니다.";
    }

    @Transactional
    public Long modify(CustomerModifyRequest request, String email) {

        Customer findCustomer = findCustomerByEmail(email);

        findCustomer.updateInfo(request);

        return findCustomer.getId();
    }

    @Transactional
    public Long delete(String email) {

        Customer findCustomer = findCustomerByEmail(email);

        customerRepository.delete(findCustomer);

        return findCustomer.getId();
    }

    @Transactional
    @SendMail(classInfo = CustomerTempPasswordResponse.class)
    public CustomerTempPasswordResponse setTempPassword(CustomerTempPasswordRequest request) {

        Customer findCustomer = customerRepository.findByEmailAndTel(request.getEmail(), request.getTel())
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        String tempPassword = "SHOPPING_MALL "+ UUID.randomUUID();

        findCustomer.setTempPassword(encoder.encode(tempPassword));

        return findCustomer.toCustomerTempPasswordResponse(tempPassword);
    }

    @Transactional(readOnly = true)
    public CustomerInfoResponse getInfo(String email) {
        Customer customer = findCustomerByEmail(email);

        return CustomerInfoResponse.toDto(customer);
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
                new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));
    }
}
