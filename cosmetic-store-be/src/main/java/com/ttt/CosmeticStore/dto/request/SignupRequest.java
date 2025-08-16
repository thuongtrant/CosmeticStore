package com.ttt.CosmeticStore.dto.request;

import com.ttt.CosmeticStore.validation.StrongPassword;
import com.ttt.CosmeticStore.validation.UniqueEmail;
import com.ttt.CosmeticStore.validation.UniqueUsername;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 20, message = "Tên đăng nhập phải từ 3-20 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên đăng nhập chỉ chứa chữ cái, số và dấu gạch dưới")
    @UniqueUsername
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Size(max = 50, message = "Email không được vượt quá 50 ký tự")
    @Email(message = "Email không đúng định dạng")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @StrongPassword
    private String password;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0[3|5|7|8|9])+([0-9]{8})$",
            message = "Số điện thoại phải đúng định dạng Việt Nam (10 số, bắt đầu bằng 03, 05, 07, 08, 09)")
    private String phone;

    @NotBlank(message = "Giới tính không được để trống")
    @Pattern(regexp = "^(Nam|Nữ)$", message = "Giới tính phải là 'Nam' hoặc 'Nữ'")
    private String gender;

    private String roleName = "CUSTOMER"; // Default role for registration
}
