package com.acon.server.global.auth.filter;

import com.acon.server.global.auth.jwt.JwtAuthenticationException;
import com.acon.server.global.dto.ErrorResponse;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 사용자가 인증되지 않은 상태에서 보호된 리소스에 접근하려고 할 때 호출

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        if (authException instanceof JwtAuthenticationException jwtEx) {
            ErrorType errorType = jwtEx.getErrorType();

            response.setStatus(errorType.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            objectMapper.writeValue(response.getOutputStream(), ErrorResponse.fail(errorType));

            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
