package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.dto.request.ShippingAddressRequest;
import com.ttt.CosmeticStore.dto.response.ShippingAddressResponse;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.service.ShippingAddressService;
import com.ttt.CosmeticStore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shipping-address")
@CrossOrigin(origins = "*")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserAddresses(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            List<ShippingAddressResponse> addresses = shippingAddressService.getUserAddresses(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("addresses", addresses);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lấy danh sách địa chỉ thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> createAddress(
            @Valid @RequestBody ShippingAddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            ShippingAddressResponse address = shippingAddressService.createAddress(user, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm địa chỉ thành công");
            response.put("address", address);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Thêm địa chỉ thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<?> updateAddress(
            @Valid @PathVariable Long addressId,
            @RequestBody ShippingAddressRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            ShippingAddressResponse address = shippingAddressService.updateAddress(addressId, user, request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật địa chỉ thành công");
            response.put("address", address);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Cập nhật địa chỉ thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            shippingAddressService.deleteAddress(addressId, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa địa chỉ thành công");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Xóa địa chỉ thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<?> setDefaultAddress(
            @PathVariable Long addressId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            ShippingAddressResponse address = shippingAddressService.setDefaultAddress(addressId, user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đặt làm địa chỉ mặc định thành công");
            response.put("address", address);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đặt địa chỉ mặc định thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/default")
    public ResponseEntity<?> getDefaultAddress(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            ShippingAddressResponse address = shippingAddressService.getDefaultAddress(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("address", address);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lấy địa chỉ mặc định thất bại: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
