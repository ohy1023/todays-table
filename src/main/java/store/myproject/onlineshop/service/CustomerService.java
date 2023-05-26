package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.dto.customer.*;
import store.myproject.onlineshop.domain.entity.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.global.redis.RedisDao;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.repository.CustomerRepository;

import java.util.concurrent.TimeUnit;


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

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.EMAIL_NOT_FOUND,
                        String.format("%s님은 존재하지 않습니다.", email)));
    }

    @Transactional
    public String join(CustomerJoinRequest request) {
        log.info("회원가입 요청 : {}", request);

        customerRepository.findByNickName(request.getNickName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_NICKNAME,
                            String.format("%s는 중복 된 닉네임입니다.", request.getNickName()));
                });

        customerRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_EMAIL, String.format("%s는 중복 된 이메일입니다.", request.getEmail()));
                });

        Customer customer = request.toEntity(encoder.encode(request.getPassword()));
        Customer savedCustomer = customerRepository.save(customer);

        CustomerDto customerDto = CustomerDto.toDto(savedCustomer);
        log.info("회원가입 완료!");
        log.info("email: {}, nickName: {}, userName: {}", savedCustomer.getEmail(), savedCustomer.getNickName(), savedCustomer.getUserName());

        return customerDto.getEmail();
    }

    @Transactional
    public CustomerLoginResponse login(CustomerLoginRequest request) {

        Customer findCustomer = findCustomerByEmail(request.getEmail());

        if (!encoder.matches(request.getPassword(), findCustomer.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "잘못된 비밀번호 입니다.");
        }

        String accessToken = jwtUtils.createAccessToken(request.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(request.getEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCustomer.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new CustomerLoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public CustomerLoginResponse reissue(CustomerTokenRequest request, String email) {

        Customer findCustomer = findCustomerByEmail(email);

        if (jwtUtils.isExpired(request.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "만료된 토큰입니다.");
        }

        String refreshToken = redisDao.getValues("RT:" + email);

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "잘못된 요청입니다");
        }
        if (!refreshToken.equals(request.getRefreshToken())) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        }

        String newAccessToken = jwtUtils.createAccessToken(findCustomer.getEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(findCustomer.getEmail());

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCustomer.getEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new CustomerLoginResponse(newAccessToken, refreshToken);

    }

    public String logout(CustomerTokenRequest request, String email) {

        Customer findCustomer = findCustomerByEmail(email);

        String accessToken = request.getAccessToken();

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "만료된 토큰입니다.");
        }

        if (jwtUtils.isValid(accessToken)) {
            throw new AppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        }

        // Token 삭제
        redisDao.deleteValues("RT:" + findCustomer.getEmail());

        int expiration = jwtUtils.getExpiration(request.getAccessToken()).intValue() / 1000;

        log.info("expiration = {}sec", expiration);

        redisDao.setValues(request.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return "로그아웃 되었습니다.";
    }

    public String userNameCheck(CustomerCheckRequest request) {
        customerRepository.findByNickName(request.getNickName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_NICKNAME,
                            String.format("%s는 중복 된 닉네임입니다.", request.getNickName()));
                });

        return "사용 가능한 닉네임 입니다.";
    }

    @Transactional
    public Long modifyUser(CustomerModifyRequest request, String email) {

        customerRepository.findByUserName(request.getNickName())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.DUPLICATE_NICKNAME,
                            String.format("%s는 중복 된 닉네임입니다.", request.getNickName()));
                });

        Customer findCustomer = findCustomerByEmail(email);

        findCustomer.updateInfo(request);

        return findCustomer.getId();
    }

    public Long deleteUser(String email) {

        Customer findCustomer = findCustomerByEmail(email);

        customerRepository.delete(findCustomer);

        return findCustomer.getId();
    }
}
