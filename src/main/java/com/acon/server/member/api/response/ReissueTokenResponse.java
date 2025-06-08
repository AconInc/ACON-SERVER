package com.acon.server.member.api.response;

public record ReissueTokenResponse(
        String accessToken,
        String refreshToken
) {

    public static ReissueTokenResponse of(
            final String accessToken,
            final String refreshToken
    ) {
        return new ReissueTokenResponse(accessToken, refreshToken);
    }
}
