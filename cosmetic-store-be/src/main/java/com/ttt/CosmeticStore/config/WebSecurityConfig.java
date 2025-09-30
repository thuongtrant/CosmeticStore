package com.ttt.CosmeticStore.config;

import com.ttt.CosmeticStore.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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


    private static final String[] STATIC_RESOURCES = {
            "/css/**", "/js/**", "/images/**", "/static/**", "/favicon.ico"
    };

    private static final String[] PUBLIC_PAGES = {
            "/", "/home", "/login", "/register", "/error", "/access-denied",
            "/forgot-password", "/reset-password", "/perform_login", "/perform_logout"
    };

    private static final String[] PUBLIC_API_ENDPOINTS = {
            "/api/auth/**", "/oauth2/**", "/login/oauth2/**","/oauth2/redirect",
            "/api/payment/momo/callback",
            "/api/payment/momo/return",
            "/api/oauth2/redirect",
            "/api/products/**","/api/filters/**",
            "/actuator/health", "/actuator/info"
    };

    private static final String[] ADMIN_WEB_ENDPOINTS = {
            "/admin/**"
    };

    private static final String[] ADMIN_API_ENDPOINTS = {
            "/api/admin/**", "/api/firebase/**"
    };

//    private static final String[] CUSTOMER_WEB_ENDPOINTS = {
//            "/customer-dashboard/**", "/customer-dashboard"
//    };
//
//    private static final String[] CUSTOMER_API_ENDPOINTS = {
//            "/api/customer/**"
//    };

    private static final String[] CUSTOMER_API_ENDPOINTS  = {
            "/api/products/**", "/api/categories/**", "/api/secure/**",
            "/api/cart/**","/api/shipping-addresses/**","/api/payment/**",
            "/api/chat/**", "/api/ai-chat/**", "/api/firebase/config"
    };

    private final UserServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Autowired
    public WebSecurityConfig(UserServiceImpl userDetailsService,
                           AuthEntryPointJwt unauthorizedHandler,
                           OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                           OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }



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


    // CSRF CONFIGURATION
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setParameterName("_csrf");
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }

    // CORS CONFIGURATION
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:3001", "http://localhost:8080"));
        config.setAllowedOriginPatterns(List.of("*"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // MAIN SECURITY CONFIGURATION
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .headers(headers -> headers
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.deny())
                        .contentTypeOptions(contentTypeOptionsConfig -> {})
                        .xssProtection(xssConfig -> {})
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true)
                        )
                )
                // CORS & CSRF
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                        .csrfTokenRepository(csrfTokenRepository())
                )

                // Exception Handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(this::handleAuthenticationException)
                        .accessDeniedHandler(this::handleAccessDeniedException)
                )

                // Session Management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry())
                        .and()
                        .sessionFixation().migrateSession()
                )

                // Authorization Rules
                .authorizeHttpRequests(authz -> authz
                        // Public resources
                        .requestMatchers(STATIC_RESOURCES).permitAll()
                        .requestMatchers(PUBLIC_PAGES).permitAll()
                        .requestMatchers(PUBLIC_API_ENDPOINTS).permitAll()

                        // Admin endpoints
                        .requestMatchers(ADMIN_WEB_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(ADMIN_API_ENDPOINTS).hasRole("ADMIN")

                        // Customer endpoints
//                        .requestMatchers(CUSTOMER_WEB_ENDPOINTS).hasRole("CUSTOMER")
                        .requestMatchers(CUSTOMER_API_ENDPOINTS).hasRole("CUSTOMER")

                        // Protected API endpoints
//                        .requestMatchers(PROTECTED_API_ENDPOINTS).authenticated()

                        // Everything else
                        .anyRequest().authenticated()
                )

                // Form Login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler((request, response, authentication) -> {
                            // Kiểm tra role và redirect phù hợp
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

                            if (isAdmin) {
                                response.sendRedirect("/admin/products");
                            } else {
                                // Customer không có trang web, redirect về login với message
                                response.sendRedirect("/login?customer=true");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // mặc định là POST
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
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

                // OAuth2 Login Configuration
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .permitAll()
                )

                // Add filters and providers
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    // EXCEPTION HANDLERS
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
        String acceptHeader = request.getHeader("Accept");

        boolean isApiRequest = requestURI.startsWith("/api/") ||
                (acceptHeader != null && acceptHeader.contains("application/json"));

        if (isApiRequest) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Access Denied\", \"message\":\"Only admin can access this resource\"}");
        } else {
            // Redirect customer về trang login với thông báo
            response.sendRedirect("/login?access-denied=true");
        }
    }
}