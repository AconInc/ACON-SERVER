package com.acon.server.appintoss.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AppInTossRatingRequest(
        @NotNull(message = "id는 필수입니다.")
        Long id,

        @NotNull(message = "rating은 필수입니다.")
        @Min(value = 1, message = "rating은 1 이상이어야 합니다.")
        @Max(value = 5, message = "rating은 5 이하여야 합니다.")
        Integer rating
) {
}
