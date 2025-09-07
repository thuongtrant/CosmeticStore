package com.ttt.CosmeticStore.controller.admin;

import com.ttt.CosmeticStore.dto.request.LoginRequest;
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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        @RequestParam(value = "customer", required = false) String customer,
                        @RequestParam(value = "access-denied", required = false) String accessDenied,Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đăng xuất thành công!");
        }

        if (customer != null) {
            model.addAttribute("customerMessage",
                    "Hệ thống quản trị chỉ dành cho Administrator. " +
                            "Tài khoản khách hàng không thể truy cập vào khu vực này. " +
                            "Vui lòng sử dụng ứng dụng di động hoặc website khách hàng.");
        }

        if (accessDenied != null) {
            model.addAttribute("accessDeniedMessage",
                    "Bạn không có quyền truy cập vào trang này. " +
                            "Chỉ có Administrator mới được phép truy cập BeautyForYou Admin.");
        }
        model.addAttribute("loginRequest", new LoginRequest());

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

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            if (userRepository.existsByUsername(signupRequest.getUsername())) {
                model.addAttribute("error", "Lỗi!!! Username đã tồn tại!");
                return "register";
            }
            if (userRepository.existsByEmail(signupRequest.getEmail())) {
                model.addAttribute("error", "Lỗi!!! Email này đã tồn tại!");
                return "register";
            }

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

}
