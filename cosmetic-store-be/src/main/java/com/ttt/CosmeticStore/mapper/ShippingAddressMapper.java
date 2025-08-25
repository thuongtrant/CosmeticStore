package com.ttt.CosmeticStore.mapper;

import com.ttt.CosmeticStore.dto.request.ShippingAddressRequest;
import com.ttt.CosmeticStore.dto.response.ShippingAddressResponse;
import com.ttt.CosmeticStore.entity.ShippingAddress;
import com.ttt.CosmeticStore.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class ShippingAddressMapper {

    public ShippingAddressResponse toResponse(ShippingAddress address) {
        if (address == null) {
            return null;
        }

        ShippingAddressResponse response = new ShippingAddressResponse();
        response.setId(address.getId());
        response.setRecipientName(address.getRecipientName());
        response.setPhoneNumber(address.getPhoneNumber());
        response.setAddressLine(address.getAddressLine());
        response.setWard(address.getWard());
        response.setDistrict(address.getDistrict());
        response.setProvince(address.getProvince());
        response.setPostalCode(address.getPostalCode());
        response.setIsDefault(address.getIsDefault());
        response.setLabel(address.getLabel());
        response.setFullAddress(address.getFullAddress());
        response.setCreatedAt(address.getCreatedAt());
        response.setProvinceCode(address.getProvinceCode());
        response.setDistrictCode(address.getDistrictCode());
        response.setWardCode(address.getWardCode());
        return response;
    }


    public List<ShippingAddressResponse> toResponseList(List<ShippingAddress> addresses) {
        if (addresses == null) {
            return null;
        }

        return addresses.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    public ShippingAddress toEntity(ShippingAddressRequest request, User user) {
        if (request == null) {
            return null;
        }

        ShippingAddress address = new ShippingAddress();
        address.setUser(user);
        address.setRecipientName(request.getRecipientName() != null ? request.getRecipientName().trim() : null);
        address.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);
        address.setAddressLine(request.getAddressLine() != null ? request.getAddressLine().trim() : null);
        address.setWard(request.getWard() != null ? request.getWard().trim() : null);
        address.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : null);
        address.setProvince(request.getProvince() != null ? request.getProvince().trim() : null);
        address.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
        address.setLabel(request.getLabel() != null ? request.getLabel().trim() : null);
        return address;
    }

    public ShippingAddress updateEntity(ShippingAddress existingAddress, ShippingAddressRequest request) {
        if (existingAddress == null || request == null) {
            return existingAddress;
        }

        existingAddress.setRecipientName(request.getRecipientName() != null ? request.getRecipientName().trim() : null);
        existingAddress.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);
        existingAddress.setAddressLine(request.getAddressLine() != null ? request.getAddressLine().trim() : null);
        existingAddress.setWard(request.getWard() != null ? request.getWard().trim() : null);
        existingAddress.setDistrict(request.getDistrict() != null ? request.getDistrict().trim() : null);
        existingAddress.setProvince(request.getProvince() != null ? request.getProvince().trim() : null);
        existingAddress.setPostalCode(request.getPostalCode() != null ? request.getPostalCode().trim() : null);
        existingAddress.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : existingAddress.getIsDefault());
        existingAddress.setLabel(request.getLabel() != null ? request.getLabel().trim() : null);

        return existingAddress;
    }

    public ShippingAddressRequest toRequest(ShippingAddress address) {
        if (address == null) return null;
        ShippingAddressRequest req = new ShippingAddressRequest();
        req.setRecipientName(address.getRecipientName());
        req.setPhoneNumber(address.getPhoneNumber());
        req.setAddressLine(address.getAddressLine());
        req.setWard(address.getWard());
        req.setDistrict(address.getDistrict());
        req.setProvince(address.getProvince());
        req.setPostalCode(address.getPostalCode());
        req.setIsDefault(address.getIsDefault());
        req.setLabel(address.getLabel());
        return req;
    }
}