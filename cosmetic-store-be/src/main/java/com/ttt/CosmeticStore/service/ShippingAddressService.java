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