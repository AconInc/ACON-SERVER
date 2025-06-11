package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "socialType은 공백일 수 없습니다.")
        String socialType,
        @NotBlank(message = "idToken은 공백일 수 없습니다.")
        String idToken
) {

}
