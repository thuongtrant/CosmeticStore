//package com.ttt.CosmeticStore.config;
//
//import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class AuthTokenFilter extends OncePerRequestFilter {
//
//    @Autowired
//    private JwtUtils jwtUtils;
//
//    @Autowired
//    private UserServiceImpl userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            String requestURI = request.getRequestURI();
//            System.out.println("🔍 AuthTokenFilter - Processing URI: " + requestURI);
//
//            // Skip static resources completely
//            if (isStaticResource(requestURI)) {
//                System.out.println("📁 Static resource - Skipping authentication: " + requestURI);
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            // Áp dụng JWT filter cho tất cả API endpoints
//            if (requestURI.startsWith("/api/") && !requestURI.startsWith("/api/auth/")) {
//                String jwt = parseJwt(request);
//                System.out.println("🔑 JWT Token extracted: " + (jwt != null ? "Present" : "Null"));
//
//                if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
//                    System.out.println("👤 Username from JWT: " + username);
//
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    SecurityContextHolder.getContext().setAuthentication(authentication);
//                    System.out.println("✅ Authentication set successfully");
//                } else {
//                    System.out.println("❌ JWT validation failed or token is null");
//                }
//            } else {
//                    // Đối với web endpoints, kiểm tra xem đã có authentication từ session chưa
//                    if (SecurityContextHolder.getContext().getAuthentication() != null) {
//                        System.out.println("🌐 Web endpoint - Authentication already exists: " +
//                                SecurityContextHolder.getContext().getAuthentication().getName());
//                    } else {
//                        System.out.println("🌐 Web endpoint - No authentication found");
//                    }
//            }
//        } catch (Exception e) {
//            System.out.println("🚨 AuthTokenFilter error: " + e.getMessage());
//            logger.error("Cannot set user authentication: {}", e);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    private boolean isStaticResource(String requestURI) {
//        return requestURI.equals("/favicon.ico") ||
//               requestURI.startsWith("/css/") ||
//               requestURI.startsWith("/js/") ||
//               requestURI.startsWith("/images/") ||
//               requestURI.startsWith("/static/") ||
//               requestURI.startsWith("/webjars/") ||
//               requestURI.equals("/robots.txt") ||
//               requestURI.equals("/sitemap.xml");
//    }
//
//    private String parseJwt(HttpServletRequest request) {
//        String headerAuth = request.getHeader("Authorization");
//
//        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
//            return headerAuth.substring(7);
//        }
//
//        return null;
//    }
//}

package com.ttt.CosmeticStore.config;

import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserServiceImpl userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // QUAN TRỌNG: Bỏ qua filter này cho tất cả non-API requests
        // Chỉ áp dụng filter cho /api/** endpoints (trừ /api/auth/**)
        boolean shouldSkip = !path.startsWith("/api/") || path.startsWith("/api/auth/");

        if (shouldSkip) {
            System.out.println("⏭️ Skipping JWT filter for: " + path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            System.out.println("🔍 JWT Filter - Processing API request: " + requestURI);

            // Chỉ xử lý JWT cho API requests
            String jwt = parseJwt(request);

            if (jwt != null) {
                System.out.println("🔑 JWT Token found");

                if (jwtUtils.validateJwtToken(jwt)) {
                    String username = jwtUtils.getUserNameFromJwtToken(jwt);
                    System.out.println("👤 JWT Username: " + username);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("✅ JWT Authentication set for user: " + username);
                    System.out.println("✅ Authorities: " + userDetails.getAuthorities());
                } else {
                    System.out.println("❌ JWT validation failed");
                }
            } else {
                System.out.println("⚠️ No JWT token found in request");
            }

        } catch (Exception e) {
            System.err.println("🚨 JWT Filter error: " + e.getMessage());
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}