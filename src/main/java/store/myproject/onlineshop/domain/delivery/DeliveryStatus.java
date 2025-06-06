package store.myproject.onlineshop.domain.delivery;

public enum DeliveryStatus {
    /** 주문 취소 */
    CANCEL,
    /** 배송 준비 중 */
    READY,
    /** 배송 중 (이동 단계) */
    SHIPPING,
    /** 배달 중 (최종 배송지에 도착하여 전달 시도 중) */
    DELIVERING,
    /** 배송 완료 */
    COMP
}