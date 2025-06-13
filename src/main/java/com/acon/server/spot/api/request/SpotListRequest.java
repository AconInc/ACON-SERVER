package com.acon.server.spot.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SpotListRequest(
        @NotNull(message = "위도는 필수입니다.")
        Double latitude,
        @NotNull(message = "경도는 필수입니다.")
        Double longitude,
        @NotNull(message = "condition은 필수입니다.")
        @Valid Condition condition
) {

    public record Condition(
            @NotBlank(message = "spotType은 공백일 수 없습니다.")
            String spotType,
            @Valid List<Filter> filterList
    ) {

        public record Filter(
                @NotNull(message = "category는 필수입니다.")
                String category,
                @NotNull(message = "optionList는 필수입니다.")
                List<String> optionList
        ) {

        }
    }
}
