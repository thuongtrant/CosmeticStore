//package com.ttt.CosmeticStore.exception;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//import java.util.Map;
//
//@ControllerAdvice
//@Slf4j
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(OrderException.class)
//    public ResponseEntity<?> handleOrderException(OrderException e) {
//        log.warn("Order exception: {}", e.getMessage());
//        return ResponseEntity.badRequest().body(
//            Map.of("success", false, "message", e.getMessage())
//        );
//    }
//
//    @ExceptionHandler(OrderProcessingException.class)
//    public ResponseEntity<?> handleOrderProcessingException(OrderProcessingException e) {
//        log.error("Order processing exception: ", e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//            Map.of("success", false, "message", "Lỗi xử lý đơn hàng")
//        );
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGenericException(Exception e) {
//        log.error("Unexpected exception: ", e);
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//            Map.of("success", false, "message", "Lỗi hệ thống")
//        );
//    }
//}
