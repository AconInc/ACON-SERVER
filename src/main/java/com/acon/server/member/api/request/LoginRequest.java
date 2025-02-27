package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "소셜 로그인 종류가 빈 값입니다.")
        String socialType,
        @NotBlank(message = "idToken이 빈 값입니다.")
        String idToken
) {

}
