package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WithdrawalReasonRequest(
        @NotBlank(message = "reason은 공백일 수 없습니다.")
        @Size(min = 1, max = 50, message = "reason은 1자 이상 50자 이하이어야 합니다.")
        String reason,

        @NotBlank(message = "refreshToken은 공백일 수 없습니다.")
        String refreshToken
) {

}
