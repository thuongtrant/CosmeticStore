package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.request.SignupRequest;
import com.ttt.CosmeticStore.entity.Role;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.repository.RoleRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("signupRequest", new SignupRequest());
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("signupRequest") SignupRequest signupRequest,
                                 BindingResult bindingResult,
                                 Model model) {

        // Kiểm tra validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            // Kiểm tra username đã tồn tại
            if (userRepository.existsByUsername(signupRequest.getUsername())) {
                model.addAttribute("error", "Lỗi!!! Username đã tồn tại!");
                return "register";
            }

            // Kiểm tra email đã tồn tại
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                model.addAttribute("error", "Lỗi!!! Email này đã tồn tại!");
                return "register";
            }

            // Tạo user mới
            User user = new User();
            user.setUsername(signupRequest.getUsername());
            user.setEmail(signupRequest.getEmail());
            user.setPassword(encoder.encode(signupRequest.getPassword()));
            user.setPhone(signupRequest.getPhone());
            user.setGender(signupRequest.getGender());

            Role userRole = roleRepository.findByName(signupRequest.getRoleName())
                    .orElseThrow(() -> new RuntimeException("Lỗi: Không thấy quyền này."));
            user.setRole(userRole);

            userRepository.save(user);

            return "redirect:/login?registerSuccess=true";

        } catch (Exception e) {
            model.addAttribute("error", "Đã có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String dashboard(Authentication authentication, Model model) {
        // Kiểm tra xác thực và quyền admin
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login?error=authentication_required";
        }

        // Kiểm tra quyền ADMIN một cách chặt chẽ
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!hasAdminRole) {
            // Log attempt for security monitoring
            System.out.println("Unauthorized access attempt to dashboard by user: " +
                             (authentication.getName() != null ? authentication.getName() : "anonymous"));
            return "redirect:/access-denied";
        }

        // Thêm thông tin admin vào model một cách an toàn
        model.addAttribute("adminName", authentication.getName());
        model.addAttribute("adminRole", "ADMIN");
        model.addAttribute("isAuthenticated", true);

        return "dashboard";
    }

    @GetMapping("/customer-dashboard")
    @PreAuthorize("hasRole('CUSTOMER')")
    public String customerDashboard(Authentication authentication, Model model) {
        // Kiểm tra thêm để đảm bảo an toàn
        if (authentication == null || !authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"))) {
            return "redirect:/login?error=access_denied";
        }

        model.addAttribute("customerName", authentication.getName());
        return "customer-dashboard";
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.getAuthorities() != null) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/dashboard";
            } else {
                return "redirect:/customer-dashboard";
            }
        }
        return "redirect:/";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này. Chỉ có Admin mới được phép.");
        return "error";
    }
}
