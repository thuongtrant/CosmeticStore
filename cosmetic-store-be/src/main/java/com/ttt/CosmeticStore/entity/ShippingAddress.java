package com.ttt.CosmeticStore.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shipping_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "address_line", nullable = false, columnDefinition = "TEXT")
    private String addressLine;

    @Column(name = "ward")
    private String ward; // Phường/Xã

    @Column(name = "district")
    private String district; // Quận/Huyện

    @Column(name = "province")
    private String province; // Tỉnh/Thành phố

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "label")
    private String label; // Nhà riêng, Văn phòng, v.v.

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Helper method để lấy địa chỉ đầy đủ
    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        if (addressLine != null) fullAddress.append(addressLine);
        if (ward != null) fullAddress.append(", ").append(ward);
        if (district != null) fullAddress.append(", ").append(district);
        if (province != null) fullAddress.append(", ").append(province);
        if (postalCode != null) fullAddress.append(" ").append(postalCode);
        return fullAddress.toString();
    }
}
