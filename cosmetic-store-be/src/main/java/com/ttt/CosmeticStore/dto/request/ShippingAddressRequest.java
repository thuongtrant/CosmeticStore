package com.ttt.CosmeticStore.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import com.ttt.CosmeticStore.validation.ValidPhoneNumber;
@Data
public class ShippingAddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(min = 2, max = 100, message = "Tên người nhận phải từ 2-100 ký tự")
    @Pattern(regexp = "^[\\p{L}\\s]+$", message = "Tên người nhận chỉ được chứa chữ cái và khoảng trắng")
    private String recipientName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @ValidPhoneNumber
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(min = 10, max = 500, message = "Địa chỉ phải từ 10-500 ký tự")
    private String addressLine;

    @Size(max = 100, message = "Phường/Xã không được vượt quá 100 ký tự")
    private String ward;

    @Size(max = 100, message = "Quận/Huyện không được vượt quá 100 ký tự")
    private String district;

    @Size(max = 100, message = "Tỉnh/Thành phố không được vượt quá 100 ký tự")
    private String province;

    @Pattern(regexp = "^[0-9]{5,6}$|^$", message = "Mã bưu điện phải là 5-6 chữ số")
    private String postalCode;

    private Boolean isDefault = false;

    @Size(max = 50, message = "Nhãn không được vượt quá 50 ký tự")
    private String label;
}