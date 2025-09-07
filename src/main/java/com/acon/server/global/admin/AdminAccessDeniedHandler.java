package com.acon.server.global.admin;

import com.acon.server.global.dto.ErrorResponse;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        ErrorType errorType = ErrorType.ACCESS_DENIED_ERROR;

        if (accessDeniedException instanceof MissingCsrfTokenException) {
            errorType = ErrorType.MISSING_CSRF_TOKEN_ERROR;
        } else if (accessDeniedException instanceof InvalidCsrfTokenException) {
            errorType = ErrorType.INVALID_CSRF_TOKEN_ERROR;
        }

        response.setStatus(errorType.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getOutputStream(), ErrorResponse.fail(errorType));
    }
}
