package com.acon.server.member.api.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SavedSpotRequest(
        @NotNull(message = "spotId는 필수입니다.")
        @Positive(message = "spotId는 양수여야 합니다.")
        Long spotId
) {

}
