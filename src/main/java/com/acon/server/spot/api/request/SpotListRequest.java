package com.acon.server.spot.api.request;

import com.acon.server.spot.domain.enums.SpotType;
import jakarta.validation.Valid;
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
            @NotNull(message = "spotType는 필수입니다.")
            SpotType spotType,

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
