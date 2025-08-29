package com.ttt.CosmeticStore.validation.impl;

import com.ttt.CosmeticStore.dto.request.CheckoutRequest;

import com.ttt.CosmeticStore.validation.ValidCheckoutRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CheckoutRequestValidator implements ConstraintValidator<ValidCheckoutRequest, CheckoutRequest> {

    @Override
    public boolean isValid(CheckoutRequest request, ConstraintValidatorContext context) {
        if (request == null) return false;

        boolean isValid = true;

        // Validate shipping address requirement
        if (!hasValidShippingAddress(request)) {
            addViolation(context, "Phải chọn địa chỉ giao hàng hoặc tạo địa chỉ mới");
            isValid = false;
        }

        // Validate MoMo specific requirements
        if ("MOMO".equals(request.getPaymentMethod())) {
            if (request.getMomoRequestType() == null) {
                addViolation(context, "Loại yêu cầu MoMo là bắt buộc khi chọn thanh toán MoMo");
                isValid = false;
            } else if (!isValidMoMoRequestType(request.getMomoRequestType())) {
                addViolation(context, "Loại yêu cầu MoMo không hợp lệ");
                isValid = false;
            }
        }

        // Validate items for duplicates
        if (request.getItems() != null && hasDuplicateProducts(request.getItems())) {
            addViolation(context, "Không được có sản phẩm trùng lặp trong đơn hàng");
            isValid = false;
        }

        return isValid;
    }

    private boolean hasValidShippingAddress(CheckoutRequest request) {
        return request.getShippingAddressId() != null ||
                request.getNewShippingAddress() != null;
    }

    private boolean isValidMoMoRequestType(String requestType) {
        return List.of("captureWallet", "payWithATM", "payWithCredit").contains(requestType);
    }

    private boolean hasDuplicateProducts(List<CheckoutRequest.CheckoutItem> items) {
        Set<Long> productIds = new HashSet<>();
        return items.stream()
                .map(CheckoutRequest.CheckoutItem::getProductId)
                .anyMatch(productId -> productId != null && !productIds.add(productId));
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
