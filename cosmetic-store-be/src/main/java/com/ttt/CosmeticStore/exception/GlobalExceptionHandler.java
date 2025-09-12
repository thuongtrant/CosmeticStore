package com.ttt.CosmeticStore.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);

        // Get the first validation error message
        String errorMessage = "Dữ liệu không hợp lệ";
        if (!ex.getBindingResult().getFieldErrors().isEmpty()) {
            FieldError fieldError = ex.getBindingResult().getFieldErrors().get(0);
            errorMessage = fieldError.getDefaultMessage();
        }

        response.put("message", errorMessage);
        response.put("error", errorMessage);

        log.warn("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<?> handleOrderException(OrderException e) {
        log.warn("Order exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
            Map.of("success", false, "message", e.getMessage())
        );
    }

    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<?> handleOrderProcessingException(OrderProcessingException e) {
        log.error("Order processing exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of("success", false, "message", "Lỗi xử lý đơn hàng")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        log.error("Unexpected exception: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of("success", false, "message", "Lỗi hệ thống")
        );
    }
}
