package com.acon.server.global.auth.security;

import com.acon.server.global.admin.AdminAccessDeniedHandler;
import com.acon.server.global.admin.AdminAuthenticationEntryPoint;
import com.acon.server.global.admin.AdminUserDetailsService;
import com.acon.server.global.auth.filter.JwtAccessDeniedHandler;
import com.acon.server.global.auth.filter.JwtAuthenticationEntryPoint;
import com.acon.server.global.auth.filter.JwtAuthenticationFilter;
import com.acon.server.global.dto.ErrorResponse;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // WebSecurity를 사용할 수 있게
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AdminAccessDeniedHandler adminAccessDeniedHandler;
    private final AdminUserDetailsService adminUserDetailsService;
    private final AdminAuthenticationEntryPoint adminAuthenticationEntryPoint;

    private final ObjectMapper objectMapper;

    private static final String[] AUTH_WHITE_LIST = {
            "/api/v1/auth/login",
            "/api/v1/auth/reissue"
    };

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    @Profile({"local", "dev"})
    public CookieCsrfTokenRepository adminCsrfTokenRepositoryDev() {
        var repo = CookieCsrfTokenRepository.withHttpOnlyFalse();

        repo.setCookieCustomizer(cookie -> cookie
                .sameSite("Lax")
                .secure(false)
                .path("/admin"));

        return repo;
    }

    @Bean
    @Profile("prod")
    public CookieCsrfTokenRepository adminCsrfTokenRepositoryProd() {
        var repo = CookieCsrfTokenRepository.withHttpOnlyFalse();

        repo.setCookieCustomizer(cookie -> cookie
                .sameSite("None")
                .secure(true)
                .path("/admin"));

        return repo;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        return http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITE_LIST).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/app-updates").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/spots").permitAll()
                        .requestMatchers(
                                mvc.pattern(HttpMethod.GET, "/api/v1/spots/{spotId:\\d+}"),
                                mvc.pattern(HttpMethod.GET, "/api/v1/spots/{spotId:\\d+}/menuboards")
                        ).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    SecurityFilterChain adminFilterChain(HttpSecurity http,
                                         SessionRegistry sessionRegistry,
                                         CookieCsrfTokenRepository adminCsrfTokenRepository) throws Exception {
        return http
                .securityMatcher("/admin/**")
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(adminCsrfTokenRepository))
                .headers(headers -> headers
                        .cacheControl(Customizer.withDefaults()))
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry))
                .userDetailsService(adminUserDetailsService)
                .requestCache(RequestCacheConfigurer::disable)
                .formLogin(form -> form
                        .loginProcessingUrl("/admin/login")
                        .successHandler(adminSuccessHandler())
                        .failureHandler(adminFailureHandler()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessHandler(adminLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")) // TODO: 추후 Spring Session – Redis (SESSION) 으로 전환하여 무중단 배포 지원
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/admin/csrf").permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin/logout").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(adminAuthenticationEntryPoint)
                        .accessDeniedHandler(adminAccessDeniedHandler))
                .build();
    }

    private AuthenticationSuccessHandler adminSuccessHandler() {
        return (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK);
    }

    private AuthenticationFailureHandler adminFailureHandler() {
        return (request, response, exception) -> {
            if (exception instanceof BadCredentialsException) {
                ErrorType errorType = ErrorType.INVALID_ID_OR_PASSWORD_ERROR;

                response.setStatus(errorType.getHttpStatus().value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());

                objectMapper.writeValue(response.getOutputStream(), ErrorResponse.fail(errorType));

                return;
            }

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    private LogoutSuccessHandler adminLogoutSuccessHandler() {
        return (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK);
    }
}
