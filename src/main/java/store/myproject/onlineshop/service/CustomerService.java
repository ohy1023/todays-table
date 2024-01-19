package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.membership.repository.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.annotation.SendMail;
import store.myproject.onlineshop.global.redis.RedisDao;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static store.myproject.onlineshop.domain.customer.CustomerRole.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final MemberShipRepository memberShipRepository;
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
    public MessageResponse join(CustomerJoinRequest request) {
        log.info("회원가입 요청 : {}", request);

        customerRepository.findByNickName(request.getNickName())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage());
                });

        customerRepository.findByEmail(request.getEmail())
                .ifPresent(customer -> {
                    throw new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage());
                });

        MemberShip memberShipBronze = memberShipRepository.findMemberShipByLevel(Level.BRONZE)
                .orElseThrow(() -> new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));

        Customer customer = request.toEntity(encoder.encode(request.getPassword()), memberShipBronze);
        Customer savedCustomer = customerRepository.save(customer);

        CustomerInfoResponse customerInfoResponse = CustomerInfoResponse.toDto(savedCustomer);
        log.info("회원가입 완료!");
        log.info("email: {}, nickName: {}, userName: {}", savedCustomer.getEmail(), savedCustomer.getNickName(), savedCustomer.getUserName());

        return new MessageResponse("회원가입 성공");
    }

    @Transactional
    public LoginResponse login(CustomerLoginRequest request) {

        Customer findCustomer = findCustomerByEmail(request.getEmail());

        if (mismatchPassword(request.getPassword(), findCustomer.getPassword())) {
            throw new AppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage());
        }

        String accessToken = jwtUtils.createAccessToken(request.getEmail());
        String refreshToken = jwtUtils.createRefreshToken(request.getEmail());

        if (accessToken == null || refreshToken == null) {
            throw new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage());
        }

        // 저장 형태 {"RT:test@test.com" , "refreshToken"}
        redisDao.setValues("RT:" + findCustomer.getEmail(), refreshToken, refreshTokenMaxAge, TimeUnit.SECONDS);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public LoginResponse reissue(TokenRequest request, String email) {

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

        return new LoginResponse(newAccessToken, newRefreshToken);

    }

    @Transactional
    public MessageResponse logout(TokenRequest request, String email) {

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

        return new MessageResponse("로그아웃 되었습니다.");
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

        String tempPassword = "SHOPPING_MALL " + UUID.randomUUID();

        findCustomer.setTempPassword(encoder.encode(tempPassword));

        return findCustomer.toCustomerTempPasswordResponse(tempPassword);
    }

    @Transactional
    public MessageResponse setNewPassword(CustomerChangePasswordRequest request, String email) {
        Customer findCustomer = findCustomerByEmail(email);
        if (mismatchPassword(request.getCurrentPassword(), findCustomer.getPassword())) {
            throw new AppException(MISMATCH_PASSWORD, MISMATCH_PASSWORD.getMessage());
        }
        findCustomer.setPassword(encoder.encode(request.getNewPassword()));
        return new MessageResponse("비밀번호가 변경되었습니다.");
    }

    @Transactional
    public MessageResponse settingAdmin(String email) {

        Customer findCustomer = findCustomerByEmail(email);

        if (findCustomer.getCustomerRole() == ROLE_ADMIN) {
            throw new AppException(ALREADY_ADMIN, ALREADY_ADMIN.getMessage());
        } else {
            findCustomer.setAdmin();
        }

        return new MessageResponse("회원의 권한을 Admin으로 설정하였습니다.");
    }

    /**
     * 주기적으로 모든 고객의 멤버십을 업데이트하는 메서드.
     * 월요일 새벽 4시에 실행됩니다.
     */
    @Transactional
    @Scheduled(cron = "0 0 4 ? * MON")
    public void updateAllMemberShip() {
        // 모든 고객 정보를 조회합니다.
        List<Customer> customers = customerRepository.findAll();

        // 각 고객에 대해 멤버십을 업데이트합니다.
        for (Customer customer : customers) {
            updateMembershipForCustomer(customer);
        }
    }

    /**
     * 특정 고객의 멤버십을 업데이트하는 메서드.
     *
     * @param customer 업데이트할 고객
     */
    private void updateMembershipForCustomer(Customer customer) {
        // 고객의 총 구매액을 조회합니다.
        BigDecimal totalPurchaseAmount = customer.getTotalPurchaseAmount();

        // 현재 총 구매액에 따라 업그레이드 가능한 멤버십을 조회합니다.
        memberShipRepository.findNextMemberShip(totalPurchaseAmount)
                .stream()
                .findFirst()
                .filter(memberShip -> totalPurchaseAmount.compareTo(memberShip.getBaseline()) > 0)
                .ifPresent(memberShip -> {
                    // 멤버십 업데이트를 로그에 기록합니다.
                    log.info("{} 회원이 {}로 업데이트 되었습니다.", customer.getId(), memberShip.getLevel());
                    // 실제 멤버십을 업데이트합니다.
                    customer.upgradeMemberShip(memberShip);
                });
    }


    public CustomerInfoResponse getInfo(String email) {
        Customer customer = findCustomerByEmail(email);

        return CustomerInfoResponse.toDto(customer);
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
                new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));
    }

    private boolean mismatchPassword(String rawPassword, String encodedPassword) {
        return !encoder.matches(rawPassword, encodedPassword);
    }
}
