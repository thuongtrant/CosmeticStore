//package com.ttt.CosmeticStore.service;
//
//import com.ttt.CosmeticStore.dto.request.ShippingAddressRequest;
//import com.ttt.CosmeticStore.dto.response.ShippingAddressResponse;
//import com.ttt.CosmeticStore.entity.ShippingAddress;
//import com.ttt.CosmeticStore.entity.User;
//import com.ttt.CosmeticStore.repository.ShippingAddressRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class ShippingAddressService {
//
//    @Autowired
//    private ShippingAddressRepository shippingAddressRepository;
//
//    public List<ShippingAddressResponse> getUserAddresses(User user) {
//        List<ShippingAddress> addresses = shippingAddressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
//        return addresses.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
//
//    @Transactional
//    public ShippingAddressResponse createAddress(User user, ShippingAddressRequest request) {
//        try {
//            // Validate input
//            if (request.getRecipientName() == null || request.getRecipientName().trim().isEmpty()) {
//                throw new RuntimeException("Tên người nhận không được để trống");
//            }
//            if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
//                throw new RuntimeException("Số điện thoại không được để trống");
//            }
//            if (request.getAddressLine() == null || request.getAddressLine().trim().isEmpty()) {
//                throw new RuntimeException("Địa chỉ không được để trống");
//            }
//
//            // Nếu đây là địa chỉ mặc định, bỏ mặc định của các địa chỉ khác
//            if (request.getIsDefault() != null && request.getIsDefault()) {
//                shippingAddressRepository.clearDefaultForUser(user);
//            }
//
//            ShippingAddress address = new ShippingAddress();
//            address.setUser(user);
//            address.setRecipientName(request.getRecipientName().trim());
//            address.setPhoneNumber(request.getPhoneNumber().trim());
//            address.setAddressLine(request.getAddressLine().trim());
//            address.setWard(request.getWard() != null ? request.getWard().trim() : null);
//            address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : null);
//            address.setProvince(request.getProvince() != null ? request.getProvince().trim() : null);
//            address.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);
//            address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
//            address.setLabel(request.getLabel() != null ? request.getLabel().trim() : null);
//
//            ShippingAddress savedAddress = shippingAddressRepository.save(address);
//            return convertToResponse(savedAddress);
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi tạo địa chỉ: " + e.getMessage(), e);
//        }
//    }
//
//    @Transactional
//    public ShippingAddressResponse updateAddress(Long addressId, User user, ShippingAddressRequest request) {
//        ShippingAddress address = shippingAddressRepository.findById(addressId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
//
//        // Kiểm tra quyền sở hữu
//        if (!address.getUser().getId().equals(user.getId())) {
//            throw new RuntimeException("Không có quyền sửa địa chỉ này");
//        }
//
//        // Nếu đây là địa chỉ mặc định, bỏ mặc định của các địa chỉ khác
//        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
//            shippingAddressRepository.clearDefaultForUser(user);
//        }
//
//        address.setRecipientName(request.getRecipientName());
//        address.setPhoneNumber(request.getPhoneNumber());
//        address.setAddressLine(request.getAddressLine());
//        address.setWard(request.getWard());
//        address.setDistrict(request.getDistrict());
//        address.setProvince(request.getProvince());
//        address.setPostalCode(request.getPostalCode());
//        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : address.getIsDefault());
//        address.setLabel(request.getLabel());
//
//        ShippingAddress savedAddress = shippingAddressRepository.save(address);
//        return convertToResponse(savedAddress);
//    }
//
//    @Transactional
//    public void deleteAddress(Long addressId, User user) {
//        ShippingAddress address = shippingAddressRepository.findById(addressId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
//
//        // Kiểm tra quyền sở hữu
//        if (!address.getUser().getId().equals(user.getId())) {
//            throw new RuntimeException("Không có quyền xóa địa chỉ này");
//        }
//
//        shippingAddressRepository.delete(address);
//    }
//
//    @Transactional
//    public ShippingAddressResponse setDefaultAddress(Long addressId, User user) {
//        ShippingAddress address = shippingAddressRepository.findById(addressId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));
//
//        // Kiểm tra quyền sở hữu
//        if (!address.getUser().getId().equals(user.getId())) {
//            throw new RuntimeException("Không có quyền thay đổi địa chỉ này");
//        }
//
//        // Bỏ mặc định của các địa chỉ khác
//        shippingAddressRepository.clearDefaultForUser(user);
//
//        // Đặt làm mặc định
//        address.setIsDefault(true);
//        ShippingAddress savedAddress = shippingAddressRepository.save(address);
//
//        return convertToResponse(savedAddress);
//    }
//
//    public ShippingAddressResponse getDefaultAddress(User user) {
//        return shippingAddressRepository.findByUserAndIsDefaultTrue(user)
//                .map(this::convertToResponse)
//                .orElse(null);
//    }
//
//    public ShippingAddress findById(Long id) {
//        return shippingAddressRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));
//    }
//
//    private ShippingAddressResponse convertToResponse(ShippingAddress address) {
//        ShippingAddressResponse response = new ShippingAddressResponse();
//        response.setId(address.getId());
//        response.setRecipientName(address.getRecipientName());
//        response.setPhoneNumber(address.getPhoneNumber());
//        response.setAddressLine(address.getAddressLine());
//        response.setWard(address.getWard());
//        response.setDistrict(address.getDistrict());
//        response.setProvince(address.getProvince());
//        response.setPostalCode(address.getPostalCode());
//        response.setIsDefault(address.getIsDefault());
//        response.setLabel(address.getLabel());
//        response.setFullAddress(address.getFullAddress());
//        response.setCreatedAt(address.getCreatedAt());
//        return response;
//    }
//}
package com.ttt.CosmeticStore.service;

import com.ttt.CosmeticStore.dto.request.ShippingAddressRequest;
import com.ttt.CosmeticStore.dto.response.ShippingAddressResponse;
import com.ttt.CosmeticStore.entity.ShippingAddress;
import com.ttt.CosmeticStore.entity.User;

import java.util.List;

public interface ShippingAddressService {

    List<ShippingAddressResponse> getUserAddresses(User user);

    ShippingAddressResponse createAddress(User user, ShippingAddressRequest request);

    ShippingAddressResponse updateAddress(Long addressId, User user, ShippingAddressRequest request);

    void deleteAddress(Long addressId, User user);

    ShippingAddressResponse setDefaultAddress(Long addressId, User user);

    ShippingAddressResponse getDefaultAddress(User user);

    ShippingAddress findById(Long id);
}