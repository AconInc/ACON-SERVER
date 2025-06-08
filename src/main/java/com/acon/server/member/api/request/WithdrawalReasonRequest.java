package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawalReasonRequest(
        @NotBlank(message = "탈퇴 이유가 빈 값입니다.")
        String reason,
        @NotBlank(message = "refreshToken이 빈 값입니다.")
        String refreshToken
) {

}
