package com.ttt.CosmeticStore.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerMessRequest {

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(max = 100, message = "Tên không được vượt quá 100 ký tự")
    private String customerName;
}
