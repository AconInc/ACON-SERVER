package com.acon.server.review.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReviewRequestV2(
        @NotNull(message = "spotId는 필수입니다.")
        @Positive(message = "spotId는 양수여야 합니다.")
        Long spotId,

        @NotBlank(message = "recommendedMenu는 공백일 수 없습니다.")
        @Size(min = 1, max = 30, message = "recommendedMenu는 1자 이상 30자 이하여야 합니다.")
        String recommendedMenu,

        @NotNull(message = "acornCount는 필수입니다.")
        @Min(value = 1, message = "acornCount는 최소 1 이상이어야 합니다.")
        @Max(value = 5, message = "acornCount는 최대 5 이하여야 합니다.")
        Integer acornCount
) {

}
