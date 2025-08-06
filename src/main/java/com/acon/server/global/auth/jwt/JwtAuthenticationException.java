package com.acon.server.global.auth.jwt;

import com.acon.server.global.exception.ErrorType;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final ErrorType errorType;

    public JwtAuthenticationException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }

    public JwtAuthenticationException(ErrorType errorType, Throwable cause) {
        super(errorType.getMessage(), cause);
        this.errorType = errorType;
    }
}
