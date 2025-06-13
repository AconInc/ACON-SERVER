package com.acon.server.global.auth;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class PrincipalHandler {

    private static final String ANONYMOUS_USER = "anonymousUser";

    public Long getMemberIdFromPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        validateMemberAuthentication(principal);

        return Long.valueOf(principal.toString());
    }

    public void validateMemberAuthentication(final Object principal) {
        if (principal.toString().equals(ANONYMOUS_USER)) {
            throw new BusinessException(ErrorType.NO_PRINCIPAL_ERROR);
        }
    }

    public boolean isGuestUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal.toString().equals(ANONYMOUS_USER);
    }
}
