package com.ttt.CosmeticStore.service.impl;

import com.ttt.CosmeticStore.dto.request.ShippingAddressRequest;
import com.ttt.CosmeticStore.dto.response.ShippingAddressResponse;
import com.ttt.CosmeticStore.entity.ShippingAddress;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.mapper.ShippingAddressMapper;
import com.ttt.CosmeticStore.repository.ShippingAddressRepository;
import com.ttt.CosmeticStore.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

    @Autowired
    private ShippingAddressRepository shippingAddressRepository;

    @Autowired
    private ShippingAddressMapper shippingAddressMapper;

    @Override
    public List<ShippingAddressResponse> getUserAddresses(User user) {
        List<ShippingAddress> addresses = shippingAddressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
        return shippingAddressMapper.toResponseList(addresses);
    }

    @Override
    @Transactional
    public ShippingAddressResponse createAddress(User user, ShippingAddressRequest request) {
        try {
            // Validate input
            validateAddressRequest(request);

            // Nếu đây là địa chỉ mặc định, bỏ mặc định của các địa chỉ khác
            if (request.getIsDefault() != null && request.getIsDefault()) {
                shippingAddressRepository.clearDefaultForUser(user);
            }

            // Convert DTO to entity using mapper
            ShippingAddress address = shippingAddressMapper.toEntity(request, user);

            // Save address
            ShippingAddress savedAddress = shippingAddressRepository.save(address);

            return shippingAddressMapper.toResponse(savedAddress);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo địa chỉ: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ShippingAddressResponse updateAddress(Long addressId, User user, ShippingAddressRequest request) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // Kiểm tra quyền sở hữu
        validateOwnership(address, user);

        // Nếu đây là địa chỉ mặc định, bỏ mặc định của các địa chỉ khác
        if (request.getIsDefault() != null && request.getIsDefault() && !address.getIsDefault()) {
            shippingAddressRepository.clearDefaultForUser(user);
        }

        // Update entity using mapper
        ShippingAddress updatedAddress = shippingAddressMapper.updateEntity(address, request);

        ShippingAddress savedAddress = shippingAddressRepository.save(updatedAddress);
        return shippingAddressMapper.toResponse(savedAddress);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, User user) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // Kiểm tra quyền sở hữu
        validateOwnership(address, user);

        shippingAddressRepository.delete(address);
    }

    @Override
    @Transactional
    public ShippingAddressResponse setDefaultAddress(Long addressId, User user) {
        ShippingAddress address = shippingAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        // Kiểm tra quyền sở hữu
        validateOwnership(address, user);

        // Bỏ mặc định của các địa chỉ khác
        shippingAddressRepository.clearDefaultForUser(user);

        // Đặt làm mặc định
        address.setIsDefault(true);
        ShippingAddress savedAddress = shippingAddressRepository.save(address);

        return shippingAddressMapper.toResponse(savedAddress);
    }

    @Override
    public ShippingAddressResponse getDefaultAddress(User user) {
        return shippingAddressRepository.findByUserAndIsDefaultTrue(user)
                .map(shippingAddressMapper::toResponse)
                .orElse(null);
    }

    @Override
    public ShippingAddress findById(Long id) {
        return shippingAddressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ giao hàng"));
    }


    private void validateAddressRequest(ShippingAddressRequest request) {
        if (request.getRecipientName() == null || request.getRecipientName().trim().isEmpty()) {
            throw new RuntimeException("Tên người nhận không được để trống");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại không được để trống");
        }
        if (request.getAddressLine() == null || request.getAddressLine().trim().isEmpty()) {
            throw new RuntimeException("Địa chỉ không được để trống");
        }
    }

    private void validateOwnership(ShippingAddress address, User user) {
        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Không có quyền thao tác với địa chỉ này");
        }
    }
}