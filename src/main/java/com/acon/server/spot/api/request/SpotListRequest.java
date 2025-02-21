package com.acon.server.spot.api.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record SpotListRequest(
        @NotNull(message = "위도는 필수입니다.")
        Double latitude,
        @NotNull(message = "경도는 필수입니다.")
        Double longitude,
        @NotNull(message = "상세 조건은 필수입니다.")
        @Valid Condition condition
) {

    public record Condition(
            String spotType,
            @Valid List<Filter> filterList,
            @NotNull(message = "도보 가능 거리는 필수입니다.")
            @Positive(message = "도보 가능 거리는 양수여야 합니다.")
            Integer walkingTime,
            @Positive(message = "가격대는 양수여야 합니다.")
            Integer priceRange
    ) {

        public record Filter(
                @NotNull(message = "카테고리는 필수입니다.")
                String category,
                @NotNull(message = "옵션 리스트는 필수입니다.")
                List<String> optionList
        ) {

        }
    }
}
