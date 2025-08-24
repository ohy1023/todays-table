package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.dto.common.MessageCode;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.dto.cusotmer.*;
import store.myproject.onlineshop.domain.membership.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.annotation.SendMail;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.domain.customer.CustomerRepository;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MemberShipRepository memberShipRepository;
    private final BCryptPasswordEncoder encoder;
    private final RedisService redisService;
    private final JwtUtils jwtUtils;
    private final MessageUtil messageUtil;

    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

    /**
     * 회원가입 처리
     */
    public MessageResponse registerCustomer(CustomerJoinRequest request) {
        validateDuplicateEmail(request.getEmail());
        validateDuplicateNickName(request.getNickName());

        List<MemberShip> memberShips = memberShipRepository.findTopByLowestBaseline(PageRequest.of(0, 1));

        if (memberShips.isEmpty()) {
            throw new AppException(MEMBERSHIP_NOT_FOUND);
        }

        Customer customer = request.toEntity(encoder.encode(request.getPassword()), memberShips.get(0));
        customerRepository.save(customer);

        return MessageResponse.of(messageUtil.get(MessageCode.CUSTOMER_JOIN));
    }

    /**
     * 로그인 처리 및 토큰 발급
     */
    public LoginResponse login(CustomerLoginRequest request) {
        Customer customer = findCustomerByEmail(request.getEmail());

        if (isPasswordMismatch(request.getPassword(), customer.getPassword())) {
            throw new AppException(INVALID_PASSWORD);
        }

        String accessToken = jwtUtils.createAccessToken(request.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(request.getEmail());

        if (accessToken == null) {
            throw new AppException(ACCESS_TOKEN_NOT_FOUND);
        } else if (refreshToken == null) {
            throw new AppException(REFRESH_TOKEN_NOT_FOUND);
        }

        redisService.setValues("RT:" + customer.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return LoginResponse.of(accessToken, refreshToken);
    }

    /**
     * 토큰 재발급
     */
    public LoginResponse reissueToken(TokenRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        if (jwtUtils.isExpired(request.getRefreshToken())) {
            throw new AppException(EXPIRED_REFRESH_TOKEN);
        }

        String storedRefreshToken = redisService.getValues("RT:" + email);

        if (ObjectUtils.isEmpty(storedRefreshToken) || !storedRefreshToken.equals(request.getRefreshToken())) {
            throw new AppException(MISMATCH_REFRESH_TOKEN);
        }

        String newAccessToken = jwtUtils.createAccessToken(customer.getEmail());
        String newRefreshToken = jwtUtils.createRefreshToken(customer.getEmail());

        redisService.setValues("RT:" + customer.getEmail(), newRefreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return LoginResponse.of(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃 처리 (access token 차단 및 refresh 삭제)
     */
    public MessageResponse logout(TokenRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        if (jwtUtils.isExpired(request.getAccessToken()) || jwtUtils.isInvalid(request.getAccessToken())) {
            throw new AppException(INVALID_ACCESS_TOKEN);
        }

        redisService.deleteValues("RT:" + customer.getEmail());
        int expiration = jwtUtils.getExpiration(request.getAccessToken()).intValue() / 1000;
        redisService.setValues(request.getAccessToken(), "logout", expiration, TimeUnit.SECONDS);

        return MessageResponse.of(messageUtil.get(MessageCode.CUSTOMER_LOGOUT));
    }

    /**
     * 이메일 중복 검사
     */
    public MessageResponse checkEmail(CustomerEmailCheckRequest request) {
        validateDuplicateEmail(request.getEmail());
        return MessageResponse.of(messageUtil.get(MessageCode.EMAIL_AVAILABLE));
    }

    /**
     * 닉네임 중복 검사
     */
    public MessageResponse checkNickName(CustomerNickNameCheckRequest request) {
        validateDuplicateNickName(request.getNickName());
        return MessageResponse.of(messageUtil.get(MessageCode.NICKNAME_AVAILABLE));
    }

    /**
     * 회원 정보 수정
     */
    public MessageResponse updateCustomerInfo(CustomerModifyRequest request, String email) {
        Customer customer = findCustomerByEmail(email);
        customer.updateInfo(request);
        return MessageResponse.of(messageUtil.get(MessageCode.CUSTOMER_MODIFIED));
    }

    /**
     * 회원 탈퇴
     */
    public MessageResponse deleteCustomer(String email) {
        Customer customer = findCustomerByEmail(email);
        customerRepository.delete(customer);
        return MessageResponse.of(messageUtil.get(MessageCode.CUSTOMER_DELETED));
    }

    /**
     * 임시 비밀번호 발급 및 메일 전송
     */
    @SendMail(classInfo = CustomerTempPasswordResponse.class)
    public CustomerTempPasswordResponse sendTempPassword(CustomerTempPasswordRequest request) {
        Customer customer = customerRepository.findByEmailAndTel(request.getEmail(), request.getTel())
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));

        String tempPassword = "TODAY_TABLE " + UUID.randomUUID();
        customer.setTempPassword(encoder.encode(tempPassword));

        return CustomerTempPasswordResponse.of(customer.getEmail(), customer.getPassword());
    }

    /**
     * 비밀번호 변경
     */
    public MessageResponse updatePassword(CustomerChangePasswordRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        if (isPasswordMismatch(request.getCurrentPassword(), customer.getPassword())) {
            throw new AppException(MISMATCH_PASSWORD);
        }

        customer.setPassword(encoder.encode(request.getNewPassword()));

        return MessageResponse.of(messageUtil.get(MessageCode.CUSTOMER_PASSWORD_MODIFIED));
    }


    /**
     * 로그인된 고객의 정보 반환
     */
    @Transactional(readOnly = true)
    public CustomerInfoResponse getCustomerInfo(String email) {
        Customer customer = findCustomerByEmail(email);
        return CustomerInfoResponse.toDto(customer);
    }

    /**
     * 이메일로 회원 조회 (없으면 예외)
     */
    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
    }

    /**
     * 비밀번호 불일치 검사
     */
    private boolean isPasswordMismatch(String rawPassword, String encodedPassword) {
        return !encoder.matches(rawPassword, encodedPassword);
    }

    private void validateDuplicateEmail(String email) {
        customerRepository.findByEmail(email).ifPresent(c -> {
            throw new AppException(DUPLICATE_EMAIL);
        });
    }

    private void validateDuplicateNickName(String nickName) {
        customerRepository.findByNickName(nickName).ifPresent(c -> {
            throw new AppException(DUPLICATE_NICKNAME);
        });
    }
}
