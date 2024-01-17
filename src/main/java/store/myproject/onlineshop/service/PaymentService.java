package store.myproject.onlineshop.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.CancelRequest;
import store.myproject.onlineshop.domain.order.dto.PostVerificationRequest;
import store.myproject.onlineshop.domain.order.dto.PreparationRequest;
import store.myproject.onlineshop.domain.order.dto.PreparationResponse;
import store.myproject.onlineshop.domain.order.repository.OrderRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    @Value("${payment.rest.api.key}")
    private String apiKey;
    @Value("${payment.rest.api.secret}")
    private String apiSecret;
    private final OrderRepository orderRepository;
    private IamportClient iamportClient;

    @PostConstruct
    public void initializeIamportClient() {
        iamportClient = new IamportClient(apiKey, apiSecret);
    }

    public PreparationResponse prepareValid(PreparationRequest request) throws IamportResponseException, IOException {
        PrepareData prepareData = new PrepareData(request.getMerchantUid(), request.getTotalPrice());
        IamportResponse<Prepare> iamportResponse = iamportClient.postPrepare(prepareData);

        log.info("결과 코드 : {}", iamportResponse.getCode());
        log.info("결과 메시지 : {}", iamportResponse.getMessage());

        if (iamportResponse.getCode() != 0) {
            throw new AppException(FAILED_PREPARE_VALID, iamportResponse.getMessage());
        }
        return PreparationResponse.builder().merchantUid(request.getMerchantUid()).build();
    }

    public MessageResponse postVerification(PostVerificationRequest request) throws IamportResponseException, IOException {
        //DB에 merchant_uid가 중복되었는지 확인
        Order order = validOrder(request.getMerchantUid());

        //DB에 있는 금액과 사용자가 결제한 금액이 같은지 확인
        BigDecimal dbAmount = calcDbAmount(order.getOrderItemList()); // db에서 가져온 금액

        IamportResponse<Payment> iamResponse = iamportClient.paymentByImpUid(request.getImpUid());
        BigDecimal paidAmount = iamResponse.getResponse().getAmount(); // 사용자가 결제한 금액

        // 금액이 다르면 결제 취소
        if (paidAmount.compareTo(dbAmount) != 0) {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(request.getImpUid());
            CancelData cancelData = createCancelData(response, BigDecimal.ZERO);
            iamportClient.cancelPaymentByImpUid(cancelData);

            throw new AppException(WRONG_PAYMENT_AMOUNT, WRONG_PAYMENT_AMOUNT.getMessage());
        }

        return new MessageResponse("사후 검증 완료되었습니다.");
    }

    private Order validOrder(String merchantUid) {
        long count = orderRepository.countByMerchantUid(merchantUid);

        if (count >= 2) {
            // 두 개 이상 존재하는 경우에 대한 로직을 추가
            throw new AppException(DUPLICATE_MERCHANT_UID, DUPLICATE_MERCHANT_UID.getMessage());
        }

        return orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));
    }

    private BigDecimal calcDbAmount(List<OrderItem> orderItemList) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItemList) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice()); // 값을 누적하기 위해 totalPrice를 업데이트
        }
        return totalPrice;
    }


    public void cancelReservation(CancelRequest cancelReq) throws IamportResponseException, IOException {
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(cancelReq.getMerchantUid());
        //cancelData 생성
        CancelData cancelData = createCancelData(response, cancelReq.getRefundAmount());
        //결제 취소
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    private CancelData createCancelData(IamportResponse<Payment> response, BigDecimal refundAmount) {
        if (refundAmount.compareTo(BigDecimal.ZERO) == 0) { //전액 환불일 경우
            return new CancelData(response.getResponse().getImpUid(), true);
        }
        //부분 환불일 경우 checksum을 입력해 준다.
        return new CancelData(response.getResponse().getImpUid(), true, refundAmount);
    }
}