package com.acon.server.member.api.response;

public record LoginResponse(
        String externalUUID,
        String accessToken,
        String refreshToken,
        boolean hasVerifiedArea
) {

    public static LoginResponse of(
            final String externalUUID,
            final String accessToken,
            final String refreshToken,
            final boolean hasVerifiedArea
    ) {
        return new LoginResponse(externalUUID, accessToken, refreshToken, hasVerifiedArea);
    }
}
