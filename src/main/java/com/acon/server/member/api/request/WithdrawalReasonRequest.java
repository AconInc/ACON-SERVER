package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;

public record WithdrawalReasonRequest(
        @NotBlank(message = "reason은 공백일 수 없습니다.")
        String reason,
        @NotBlank(message = "refreshToken은 공백일 수 없습니다.")
        String refreshToken
) {

}