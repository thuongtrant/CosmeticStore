package com.ttt.CosmeticStore.config;

import com.ttt.CosmeticStore.entity.Role;
import com.ttt.CosmeticStore.entity.User;
import com.ttt.CosmeticStore.repository.RoleRepository;
import com.ttt.CosmeticStore.repository.UserRepository;
import com.ttt.CosmeticStore.service.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {


        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User = oauth2Token.getPrincipal();
        String registrationId = oauth2Token.getAuthorizedClientRegistrationId();

        // Extract user info from OAuth2 provider
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = extractEmail(attributes, registrationId);
        String name = extractName(attributes, registrationId);


        if (email == null || email.isEmpty()) {
            // Redirect to error page if email is not available
            getRedirectStrategy().sendRedirect(request, response,
                "http://localhost:3000/login?error=email_required");
            return;
        }

        // Find or create user
        User user = findOrCreateUser(email, name, registrationId);
        // Generate JWT token
        UserPrincipal userPrincipal = UserPrincipal.build(user);
        String jwt = jwtUtils.generateTokenFromUsername(user.getUsername());

        // Redirect to frontend OAuth2 redirect component with JWT token
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", jwt)
                .queryParam("role", user.getRole().getName())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private String extractEmail(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("email");
        } else if ("facebook".equals(registrationId)) {
            return (String) attributes.get("email");
        }
        return null;
    }

    private String extractName(Map<String, Object> attributes, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) attributes.get("name");
        } else if ("facebook".equals(registrationId)) {
            return (String) attributes.get("name");
        }
        return "Unknown User";
    }

    private User findOrCreateUser(String email, String name, String provider) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // Create new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(generateUsername(email, provider));
        newUser.setPassword(""); // OAuth2 users don't have password
        newUser.setPhone(""); // Will be updated later
        newUser.setGender(""); // Will be updated later

        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());

        // Set default role as CUSTOMER
        Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: Role CUSTOMER is not found."));
        newUser.setRole(customerRole);

        return userRepository.save(newUser);
    }

    private String generateUsername(String email, String provider) {
        String baseUsername = email.split("@")[0] + "_" + provider;
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + "_" + counter;
            counter++;
        }

        return username;
    }
}
