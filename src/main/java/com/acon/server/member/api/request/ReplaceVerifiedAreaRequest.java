package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReplaceVerifiedAreaRequest(
        @NotNull(message = "verifiedAreaId는 필수입니다.")
        @Positive(message = "verifiedAreaId는 양수여야 합니다.")
        Long verifiedAreaId,
        @NotNull(message = "위도는 필수입니다.")
        Double latitude,
        @NotNull(message = "경도는 필수입니다.")
        Double longitude
) {

}
