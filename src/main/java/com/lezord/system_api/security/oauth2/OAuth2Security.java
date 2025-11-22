package com.lezord.system_api.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lezord.system_api.config.JwtConfig;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.service.impl.ApplicationUserServiceImpl;
import com.lezord.system_api.util.StandardResponseDTO;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;


@Component
@RequiredArgsConstructor
public class OAuth2Security{

    private final ApplicationUserServiceImpl userService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;


    public void oauth2SuccessHandler(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        ApplicationUser user = userService.processOAuthPostLogin(oauthUser);
        UserDetails userDetails = userService.loadUserByUsername(user.getUsername());

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())))
                .signWith(secretKey)
                .compact();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtConfig.getTokenPrefix() + token);

        if (user.getRoles().stream().noneMatch(r -> r.getRoleName().equals("ADMIN") || r.getRoleName().equals("TRAINER"))) {
            String redirectUri = "http://localhost:4200/#/security/process/oauth2-redirect?token=" + jwtConfig.getTokenPrefix() + token;
            response.sendRedirect(redirectUri);
        }

        StandardResponseDTO responseBody = StandardResponseDTO.builder()
                .code(HttpStatus.OK.value())
                .message("OAuth2 Login Success")
                .data(userService.getApplicationUserByUsername(oauthUser))
                .build();

        new ObjectMapper().writeValue(response.getWriter(), responseBody);


    }
}
