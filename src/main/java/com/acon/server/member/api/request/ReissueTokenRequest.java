package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueTokenRequest(
        @NotBlank(message = "refreshToken은 공백일 수 없습니다.")
        String refreshToken
) {

}
