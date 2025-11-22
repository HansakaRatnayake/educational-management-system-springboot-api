package com.lezord.system_api.security;


import com.lezord.system_api.config.JwtConfig;
import com.lezord.system_api.jwt.JwtTokenVerifier;
import com.lezord.system_api.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import com.lezord.system_api.security.oauth2.OAuth2Security;
import com.lezord.system_api.service.impl.ApplicationUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


import javax.crypto.SecretKey;
import java.util.List;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig{


    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserServiceImpl userService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final OAuth2Security oAuth2Security;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http, AuthenticationManager authenticationManager
    ) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("*", "Content-Type","App-Type"));
        corsConfiguration.setAllowedOrigins(List.of("http://www.nozomi.lk", "http://nozomi.lk", "https://www.nozomi.lk", "https://nozomi.lk", "https://admin.nozomi.lk", "http://admin.nozomi.lk"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTION", "PUT", "PATCH"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization"));

        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> corsConfiguration))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager, jwtConfig, secretKey,userService))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/users/visitor/**").permitAll()
                        .requestMatchers("/api/v1/roles/visitor/**").permitAll()
                        .requestMatchers("/api/v1/statistics/visitors/**").permitAll()
                        .requestMatchers("/api/v1/course-content-types").permitAll()
                        .requestMatchers("/api/v1/course-stages/all-stages/**").permitAll()
                        .requestMatchers("/api/v1/users/forgot-password-request-code/**").permitAll()
                        .requestMatchers("/api/v1/users/verify-reset/**").permitAll()
                        .requestMatchers("/api/v1/users/reset-password").permitAll()
                        .requestMatchers("/api/v1/pending-instructors/register/request").permitAll()
                        .requestMatchers("/api/v1/statuses/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/faqs").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/get-in-touch").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/v1/courses").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/latest/visitor/view").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/by-intake/visitor/view/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/active-program-count").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/change-course-status/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/courses/{courseId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/course-stages").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/intake-installments/by-intake/").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/success-stories").permitAll()
                        .requestMatchers("/api/v1/instructors/visitors/**").permitAll()
                        .requestMatchers("/api/v1/instructors/visitors/**").permitAll()
                        .requestMatchers("/api/v1/test/**").permitAll()
                        .requestMatchers("/api/v1/testv1/**").permitAll()
                        .requestMatchers("/login/oauth2/code/google/**").permitAll()
                        .requestMatchers("/api/v1/payments/notify").permitAll()
                        .anyRequest()
                        .authenticated());
//                .oauth2Login(oauth2 -> oauth2
//                        .successHandler(oAuth2Security::oauth2SuccessHandler)
//                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userService);
        return provider;
    }
}