////package com.ttt.CosmeticStore.config;
////
////import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.authentication.AuthenticationManager;
////import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
////import org.springframework.security.config.Customizer;
////import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.core.session.SessionRegistry;
////import org.springframework.security.core.session.SessionRegistryImpl;
////import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
////import org.springframework.security.crypto.password.PasswordEncoder;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
////import org.springframework.web.cors.CorsConfiguration;
////import org.springframework.web.cors.CorsConfigurationSource;
////import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
////
////import java.util.Arrays;
////
////@Configuration
////@EnableWebSecurity
////@EnableMethodSecurity
////public class WebSecurityConfig {
////
////    @Autowired
////    private UserServiceImpl userDetailsService;
////
////    @Autowired
////    private AuthEntryPointJwt unauthorizedHandler;
////
////    @Bean
////    public AuthTokenFilter authenticationJwtTokenFilter() {
////        return new AuthTokenFilter();
////    }
////
////    @Bean
////    public DaoAuthenticationProvider authenticationProvider() {
////        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
////        authProvider.setUserDetailsService(userDetailsService);
////        authProvider.setPasswordEncoder(passwordEncoder());
////        return authProvider;
////    }
////
////    @Bean
////    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
////        return authConfig.getAuthenticationManager();
////    }
////
////    @Bean
////    public PasswordEncoder passwordEncoder() {
////        return new BCryptPasswordEncoder();
////    }
////
////    @Bean
////    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////            .cors(Customizer.withDefaults())
////            .csrf(csrf -> csrf
////                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////                .ignoringRequestMatchers("/api/**") // Disable CSRF for API endpoints
////            )
////            .exceptionHandling(exception -> exception
////                .authenticationEntryPoint(unauthorizedHandler)
////                .accessDeniedHandler((request, response, accessDeniedException) -> {
////                    String requestURI = request.getRequestURI();
////                    if (requestURI.startsWith("/api/")) {
////                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
////                        response.getWriter().write("Access Denied");
////                    } else {
////                        response.sendRedirect("/access-denied");
////                    }
////                })
////            )
////            .sessionManagement(session -> session
////                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Cho phép session cho web forms
////                .maximumSessions(1)
////                .maxSessionsPreventsLogin(false)
////                .sessionRegistry(sessionRegistry())
////                .and()
////                .sessionFixation().migrateSession()
////            )
////            .authorizeHttpRequests(authz -> authz
////                // Public endpoints - API và Web
////                .requestMatchers("/api/auth/**").permitAll()
////                .requestMatchers("/api/test/public").permitAll()
////                .requestMatchers("/", "/home", "/login", "/register", "/error", "/access-denied").permitAll()
////                .requestMatchers("/forgot-password", "/reset-password").permitAll()
////                .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico").permitAll()
////                .requestMatchers("/perform_login", "/perform_logout").permitAll()
////
////                // Admin endpoints - đặt trước để có độ ưu tiên cao
////                .requestMatchers("/admin/**").hasRole("ADMIN")
////                .requestMatchers("/dashboard", "/dashboard/**").hasRole("ADMIN")
////                .requestMatchers("/api/test/admin").hasRole("ADMIN")
////
////                // API endpoints cho products (cần authentication)
////                .requestMatchers("/api/products/**").authenticated()
////                .requestMatchers("/api/categories/**").authenticated()
////
////                // API endpoints cho user profile (cần authentication)
////                .requestMatchers("/api/secure/user/**").authenticated()
////
////                // API endpoints cho cart (cần authentication)
////                .requestMatchers("/api/cart/**").authenticated()
////
////                // Customer endpoints
//////                .requestMatchers("/customer-dashboard", "/customer-dashboard/**").hasRole("CUSTOMER")
//////                .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
//////                .requestMatchers("/api/test/customer").hasRole("CUSTOMER")
////
////                // Protected endpoints
//////                .requestMatchers("/api/test/protected").authenticated()
////                .anyRequest().permitAll() // Cho phép tất cả các request khác
////            )
////            .formLogin(form -> form
////                .loginPage("/login")
////                .loginProcessingUrl("/perform_login")
////                .usernameParameter("username")
////                .passwordParameter("password")
////                .successHandler((request, response, authentication) -> {
////                    // Custom success handler
////                    boolean isAdmin = authentication.getAuthorities().stream()
////                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
////
////                    if (isAdmin) {
////                        response.sendRedirect("/dashboard");
////                    } else {
////                        response.sendRedirect("/customer-dashboard");
////                    }
////                })
////                .failureHandler((request, response, exception) -> {
////                    response.sendRedirect("/login?error=true");
////                })
////                .permitAll()
////            )
////            .logout(logout -> logout
////                .logoutUrl("/perform_logout")
////                .logoutSuccessUrl("/login?logout=true")
////                .invalidateHttpSession(true)
////                .deleteCookies("JSESSIONID", "remember-me")
////                .permitAll()
////            )
////            .rememberMe(rememberMe -> rememberMe
////                .key("BeautyForYou-RememberMe-Secret-Key")
////                .userDetailsService(userDetailsService)
////                .rememberMeParameter("remember-me")
////                .tokenValiditySeconds(2592000) // 30 ngày
////                .rememberMeCookieName("remember-me")
////            );
////
////        // Thêm JWT filter chỉ cho API endpoints
////        http.authenticationProvider(authenticationProvider());
////        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
////    @Bean
////    public CorsConfigurationSource corsConfigurationSource() {
////        CorsConfiguration configuration = new CorsConfiguration();
////        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
////        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
////        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
////        configuration.setAllowCredentials(true);
////        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
////        source.registerCorsConfiguration("/**", configuration);
////        return source;
////    }
////
////    @Bean
////    public SessionRegistry sessionRegistry() {
////        return new SessionRegistryImpl();
////    }
////}
//
//package com.ttt.CosmeticStore.config;
//
//import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.session.SessionRegistry;
//import org.springframework.security.core.session.SessionRegistryImpl;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig {
//
//    @Autowired
//    private UserServiceImpl userDetailsService;
//
//    @Autowired
//    private AuthEntryPointJwt unauthorizedHandler;
//
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setParameterName("_csrf");
//        repository.setHeaderName("X-CSRF-TOKEN");
//        return repository;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(Customizer.withDefaults())
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**", "/admin/**")
//                )
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint((request, response, authException) -> {
//                            String requestURI = request.getRequestURI();
//                            String acceptHeader = request.getHeader("Accept");
//
//                            if (requestURI.startsWith("/api/") ||
//                                    (acceptHeader != null && acceptHeader.contains("application/json"))) {
//                                unauthorizedHandler.commence(request, response, authException);
//                            } else {
//                                response.sendRedirect("/login");
//                            }
//                        })
//                        .accessDeniedHandler((request, response, accessDeniedException) -> {
//                            String requestURI = request.getRequestURI();
//
//                            // Lấy authentication từ SecurityContext thay vì request
//                            var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
//
//                            System.out.println("🚫 Access Denied for URI: " + requestURI);
//                            System.out.println("🚫 Auth from SecurityContext: " + auth);
//                            System.out.println("🚫 Method: " + request.getMethod());
//                            System.out.println("🚫 Content-Type: " + request.getContentType());
//
//                            if (requestURI.startsWith("/api/")) {
//                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//                                response.getWriter().write("Access Denied");
//                            } else {
//                                response.sendRedirect("/access-denied");
//                            }
//                        })
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                        .maximumSessions(1)
//                        .maxSessionsPreventsLogin(false)
//                        .sessionRegistry(sessionRegistry())
//                        .and()
//                        .sessionFixation().migrateSession()
//                        .invalidSessionUrl("/login?expired=true")
//                )
//                .authorizeHttpRequests(authz -> authz
//                        // Static resources và public pages
//                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico").permitAll()
//                        .requestMatchers("/", "/home", "/login", "/register", "/error", "/access-denied").permitAll()
//                        .requestMatchers("/forgot-password", "/reset-password").permitAll()
//                        .requestMatchers("/perform_login", "/perform_logout").permitAll()
//
//                        // Public API endpoints
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/test/public").permitAll()
//
//                        // Admin pages - QUAN TRỌNG: Cho phép cả GET và POST
//                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
//                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")
//
//                        .requestMatchers("/dashboard/**").hasRole("ADMIN")
//                        .requestMatchers("/dashboard").hasRole("ADMIN")
//                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/api/test/admin").hasRole("ADMIN")
//
//                        // Customer pages
//                        .requestMatchers("/customer-dashboard/**").hasRole("CUSTOMER")
//                        .requestMatchers("/customer-dashboard").hasRole("CUSTOMER")
//                        .requestMatchers("/api/customer/**").hasRole("CUSTOMER")
//                        .requestMatchers("/api/test/customer").hasRole("CUSTOMER")
//
//                        // Protected API endpoints
//                        .requestMatchers("/api/products/**").authenticated()
//                        .requestMatchers("/api/categories/**").authenticated()
//                        .requestMatchers("/api/secure/**").authenticated()
//                        .requestMatchers("/api/cart/**").authenticated()
//                        .requestMatchers("/api/test/protected").authenticated()
//
//                        // Everything else
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login")
//                        .loginProcessingUrl("/perform_login")
//                        .usernameParameter("username")
//                        .passwordParameter("password")
//                        .defaultSuccessUrl("/dashboard", false)
//                        .successHandler((request, response, authentication) -> {
//                            System.out.println("✅ Login successful for: " + authentication.getName());
//                            System.out.println("✅ Authorities: " + authentication.getAuthorities());
//
//                            boolean isAdmin = authentication.getAuthorities().stream()
//                                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
//
//                            if (isAdmin) {
//                                response.sendRedirect("/dashboard");
//                            } else {
//                                response.sendRedirect("/customer-dashboard");
//                            }
//                        })
//                        .failureUrl("/login?error=true")
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/perform_logout")
//                        .logoutSuccessUrl("/login?logout=true")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID", "remember-me")
//                        .permitAll()
//                )
//                .rememberMe(rememberMe -> rememberMe
//                        .key("BeautyForYou-RememberMe-Secret-Key")
//                        .userDetailsService(userDetailsService)
//                        .rememberMeParameter("remember-me")
//                        .tokenValiditySeconds(2592000)
//                        .rememberMeCookieName("remember-me")
//                );
//
//        // Thêm authentication provider
//        http.authenticationProvider(authenticationProvider());
//
//        // Thêm JWT filter CHỈ cho API endpoints
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }
//}

package com.ttt.CosmeticStore.config;

import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    // ================================
    // ENDPOINT CONSTANTS (Học từ Spring MVC)
    // ================================

    private static final String[] STATIC_RESOURCES = {
            "/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico"
    };

    private static final String[] PUBLIC_PAGES = {
            "/", "/home", "/login", "/register", "/error", "/access-denied",
            "/forgot-password", "/reset-password", "/perform_login", "/perform_logout"
    };

    private static final String[] PUBLIC_API_ENDPOINTS = {
            "/api/auth/**", "/api/test/public"
    };

    private static final String[] ADMIN_WEB_ENDPOINTS = {
            "/admin/**", "/dashboard/**", "/dashboard"
    };

    private static final String[] ADMIN_API_ENDPOINTS = {
            "/api/admin/**", "/api/test/admin"
    };

    private static final String[] CUSTOMER_WEB_ENDPOINTS = {
            "/customer-dashboard/**", "/customer-dashboard"
    };

    private static final String[] CUSTOMER_API_ENDPOINTS = {
            "/api/customer/**", "/api/test/customer"
    };

    private static final String[] PROTECTED_API_ENDPOINTS = {
            "/api/products/**", "/api/categories/**", "/api/secure/**",
            "/api/cart/**", "/api/test/protected"
    };

    // ================================
    // DEPENDENCIES
    // ================================

    private final UserServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    public WebSecurityConfig(UserServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    // ================================
    // AUTHENTICATION BEANS
    // ================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // ================================
    // CSRF CONFIGURATION
    // ================================

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setParameterName("_csrf");
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }

    // ================================
    // CORS CONFIGURATION (Đơn giản hóa như Spring MVC)
    // ================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ================================
    // MAIN SECURITY CONFIGURATION
    // ================================

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // CORS & CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/admin/**")
                        .csrfTokenRepository(csrfTokenRepository())
                )

                // Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this::handleAuthenticationException)
                        .accessDeniedHandler(this::handleAccessDeniedException)
                )

                // Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                        .and()
                        .sessionFixation().migrateSession()
                        .invalidSessionUrl("/login?expired=true")
                )

                // Authorization Rules (Sử dụng constants như Spring MVC)
                .authorizeHttpRequests(authz -> authz
                        // Public resources
                        .requestMatchers(STATIC_RESOURCES).permitAll()
                        .requestMatchers(PUBLIC_PAGES).permitAll()
                        .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()

                        // Admin endpoints
                        .requestMatchers(HttpMethod.GET, ADMIN_WEB_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, ADMIN_WEB_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, ADMIN_WEB_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ADMIN_WEB_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(ADMIN_API_ENDPOINTS).hasRole("ADMIN")

                        // Customer endpoints
                        .requestMatchers(CUSTOMER_WEB_ENDPOINTS).hasRole("CUSTOMER")
                        .requestMatchers(CUSTOMER_API_ENDPOINTS).hasRole("CUSTOMER")

                        // Protected API endpoints
                        .requestMatchers(PROTECTED_API_ENDPOINTS).authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Form Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/dashboard", false)
                        .successHandler((request, response, authentication) -> {
                            System.out.println("✅ Login successful for: " + authentication.getName());

                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

                            String redirectUrl = isAdmin ? "/dashboard" : "/customer-dashboard";
                            response.sendRedirect(redirectUrl);
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )

                // Remember Me
                .rememberMe(rememberMe -> rememberMe
                        .key("BeautyForYou-RememberMe-Secret-Key")
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(2592000) // 30 days
                        .rememberMeCookieName("remember-me")
                )

                // Add filters and providers
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ================================
    // EXCEPTION HANDLERS
    // ================================

    private void handleAuthenticationException(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.core.AuthenticationException authException
    ) throws java.io.IOException, jakarta.servlet.ServletException {

        String requestURI = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");

        boolean isApiRequest = requestURI.startsWith("/api/") ||
                (acceptHeader != null && acceptHeader.contains("application/json"));

        if (isApiRequest) {
            unauthorizedHandler.commence(request, response, authException);
        } else {
            response.sendRedirect("/login");
        }
    }

    private void handleAccessDeniedException(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            org.springframework.security.access.AccessDeniedException accessDeniedException
    ) throws java.io.IOException {

        String requestURI = request.getRequestURI();
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("🚫 Access Denied for URI: " + requestURI);
        System.out.println("🚫 User: " + (authentication != null ? authentication.getName() : "Anonymous"));

        if (requestURI.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Access Denied\",\"message\":\"Insufficient privileges\"}");
        } else {
            response.sendRedirect("/access-denied");
        }
    }
}