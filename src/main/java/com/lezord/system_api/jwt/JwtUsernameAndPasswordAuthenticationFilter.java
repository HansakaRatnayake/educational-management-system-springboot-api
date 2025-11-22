package com.lezord.system_api.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lezord.system_api.config.JwtConfig;
import com.lezord.system_api.dto.request.RequestApplicationUserLoginDTO;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.exception.InvalidAccessException;
import com.lezord.system_api.security.SupportSpringApplicationUser;
import com.lezord.system_api.service.impl.ApplicationUserServiceImpl;
import com.lezord.system_api.util.StandardResponseDTO;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

@RequiredArgsConstructor
public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final ApplicationUserServiceImpl applicationUserService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            String appType = request.getHeader("App-Type");
            if (appType == null || appType.isEmpty()) {
                throw new RuntimeException("AppType is required");
            }

            RequestApplicationUserLoginDTO requestApplicationUserLoginDto = new ObjectMapper()
                    .readValue(request.getInputStream(), RequestApplicationUserLoginDTO.class);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    requestApplicationUserLoginDto.getUsername(),
                    requestApplicationUserLoginDto.getPassword()
            );

            Authentication authResult = authenticationManager.authenticate(authentication);

            SupportSpringApplicationUser supportSpringApplicationUser = (SupportSpringApplicationUser) authResult.getPrincipal();

            ApplicationUser applicationUser = applicationUserService.findById(supportSpringApplicationUser.getUserId());


            if (appType.equals("client") && applicationUser.getRoles().stream().noneMatch(r -> r.getRoleName().equals("STUDENT"))) {
                writeResponse(response,"Login Failed...Only STUDENT role users can access the client application",HttpStatus.FORBIDDEN,null);
                return null;
            }

            if (appType.equals("admin") && applicationUser.getRoles().stream().noneMatch(r -> r.getRoleName().equals("ADMIN") || r.getRoleName().equals("TRAINER"))) {
                writeResponse(response,"Login Failed...Only ADMIN,TEACHER role users can access the admin application",HttpStatus.FORBIDDEN,null);
                return null;
            }

            if (applicationUser.isOauthUser()){
                writeResponse(response,"Login Failed...Incorrect username or password",HttpStatus.FORBIDDEN,null);
                return null;
            }

            return authResult;
        } catch (IOException e) {
            throw new InvalidAccessException(e.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .setIssuedAt(new Date())
                .setExpiration(
                        java.sql.Date.valueOf(LocalDate.now()
                                .plusDays(jwtConfig.getTokenExpirationAfterDays()))
                )
                .signWith(secretKey)
                .compact();

        response.addHeader(HttpHeaders.AUTHORIZATION, jwtConfig.getTokenPrefix() + token);

        SupportSpringApplicationUser supportSpringApplicationUser = (SupportSpringApplicationUser) authResult.getPrincipal();

        writeResponse(response,"Login Success",HttpStatus.OK, applicationUserService.getApplicationUserByUsername(supportSpringApplicationUser.getUsername()));

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }


    private void writeResponse(HttpServletResponse response, String message, HttpStatus httpStatus, Object data) throws IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        StandardResponseDTO responseBody = StandardResponseDTO.builder()
                .code(httpStatus.value())
                .message(message)
                .data(data)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(responseBody));
    }
}
