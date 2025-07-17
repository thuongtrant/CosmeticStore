package com.ttt.CosmeticStore.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicAccess() {
        return "Đây là nội dung công khai - không cần đăng nhập!";
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ADMIN')")
    public String customerAccess() {
        return "Nội dung dành cho khách hàng - cần quyền CUSTOMER hoặc ADMIN.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminAccess() {
        return "Nội dung quản trị viên - chỉ dành cho ADMIN.";
    }

    @GetMapping("/protected")
    @PreAuthorize("isAuthenticated()")
    public String protectedAccess() {
        return "Nội dung được bảo vệ - cần đăng nhập với bất kỳ role nào.";
    }
}
