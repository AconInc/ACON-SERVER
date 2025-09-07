package com.acon.server.admin.api.response;

public record CsrfTokenResponse(
        String headerName,
        String parameterName,
        String token
) {

    public static CsrfTokenResponse of(
            String headerName,
            String parameterName,
            String token
    ) {
        return new CsrfTokenResponse(headerName, parameterName, token);
    }
}
