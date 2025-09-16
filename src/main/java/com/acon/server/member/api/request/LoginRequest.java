package com.acon.server.member.api.request;

import com.acon.server.member.domain.enums.SocialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "socialType은 필수입니다.")
        SocialType socialType,

        @NotBlank(message = "idToken은 공백일 수 없습니다.")
        String idToken
) {

}
