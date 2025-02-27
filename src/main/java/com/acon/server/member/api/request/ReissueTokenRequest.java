package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        @NotBlank(message = "refreshToken이 빈 값입니다.")
        String refreshToken
) {

}
