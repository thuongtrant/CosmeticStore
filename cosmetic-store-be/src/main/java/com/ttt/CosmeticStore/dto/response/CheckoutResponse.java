//package com.ttt.CosmeticStore.dto.response;
//
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//@Builder
//public class CheckoutResponse {
//    private String sessionId;
//    private String orderNumber;
//    private OrderStatus status;
//
//    // MoMo specific fields
//    private String payUrl;
//    private String qrCodeUrl;
//    private String deeplink;
//
//    // Order specific fields
//    private OrderResponse order;
//
//    public enum OrderStatus {
//        PENDING_PAYMENT,
//        PAYMENT_PROCESSING,
//        COMPLETED,
//        FAILED,
//        CANCELLED
//    }
//}