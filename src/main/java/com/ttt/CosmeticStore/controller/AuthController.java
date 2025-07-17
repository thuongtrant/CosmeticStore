package com.ttt.CosmeticStore.controller;

import com.ttt.CosmeticStore.config.JwtUtils;
import com.ttt.CosmeticStore.dto.response.JwtResponse;
import com.ttt.CosmeticStore.dto.request.LoginRequest;
import com.ttt.CosmeticStore.dto.request.SignupRequest;
import com.ttt.CosmeticStore.entity.Role;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.repository.RoleRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Lỗi!!! Username đã tồn tại!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Lỗi!!! Email này đã tồn tại!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhone(signUpRequest.getPhone());
        user.setGender(signUpRequest.getGender());

        Role userRole = roleRepository.findByName(signUpRequest.getRoleName())
                .orElseThrow(() -> new RuntimeException("Lỗi: Không thấy quyền này."));
        user.setRole(userRole);

        userRepository.save(user);

        return ResponseEntity.ok("Đăng ký thành công!");
    }


    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        // Kiểm tra xác thực và quyền admin
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
        }

        // Kiểm tra quyền ADMIN một cách chặt chẽ
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminRole) {
            return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền truy cập. Chỉ Admin mới được phép."));
        }

        // Tạo response data cho dashboard
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("adminName", authentication.getName());
        dashboardData.put("role", "ADMIN");
        dashboardData.put("message", "Chào mừng đến Dashboard Admin từ AuthController!");
        dashboardData.put("timestamp", System.currentTimeMillis());
        dashboardData.put("endpoint", "/api/auth/dashboard");

        // Thêm thống kê mock data
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", 150);
        statistics.put("totalProducts", 89);
        statistics.put("totalOrders", 245);
        statistics.put("monthlyRevenue", 125000000);

        dashboardData.put("statistics", statistics);

        return ResponseEntity.ok(dashboardData);
    }

//    // THÊM ENDPOINT XỬ LÝ FORM LOGIN TỪ UI
//    @PostMapping("/web-login")
//    public String handleWebLogin(@RequestParam String username,
//                                @RequestParam String password,
//                                HttpServletRequest request,
//                                HttpServletResponse response) {
//        try {
//            // Xác thực người dùng
//            Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(username, password)
//            );
//
//            // Set authentication context
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            // Kiểm tra role và redirect
//            boolean isAdmin = authentication.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//            if (isAdmin) {
//                return "redirect:/dashboard";  // Chuyển đến trang dashboard cho ADMIN
//            } else {
//                return "redirect:/customer-dashboard";  // Chuyển đến trang customer dashboard
//            }
//
//        } catch (Exception e) {
//            // Đăng nhập thất bại
//            return "redirect:/login?error=true";
//        }
//    }
}
//    // THÊM API ENDPOINT CHO ADMIN DASHBOARD TEST
//    @GetMapping("/admin/dashboard")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> getAdminDashboard(Authentication authentication) {
//        // Kiểm tra xác thực và quyền admin
//        if (authentication == null || !authentication.isAuthenticated()) {
//            return ResponseEntity.status(401).body(Map.of("error", "Chưa đăng nhập"));
//        }
//
//        // Kiểm tra quyền ADMIN một cách chặt chẽ
//        boolean hasAdminRole = authentication.getAuthorities().stream()
//                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
//
//        if (!hasAdminRole) {
//            return ResponseEntity.status(403).body(Map.of("error", "Bạn không có quyền truy cập. Chỉ Admin mới được phép."));
//        }
//
//        // Tạo response data cho dashboard
//        Map<String, Object> dashboardData = new HashMap<>();
//        dashboardData.put("adminName", authentication.getName());
//        dashboardData.put("role", "ADMIN");
//        dashboardData.put("message", "Chào mừng đến Dashboard Admin!");
//        dashboardData.put("timestamp", System.currentTimeMillis());
//
//        // Thêm thống kê mock data
//        Map<String, Object> statistics = new HashMap<>();
//        statistics.put("totalUsers", 150);
//        statistics.put("totalProducts", 89);
//        statistics.put("totalOrders", 245);
//        statistics.put("monthlyRevenue", 125000000);
//
//        dashboardData.put("statistics", statistics);
//
//        return ResponseEntity.ok(dashboardData);
//    }
