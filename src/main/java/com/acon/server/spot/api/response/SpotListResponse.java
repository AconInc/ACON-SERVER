package com.acon.server.spot.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public record SpotListResponse(
        List<RecommendedSpot> spotList
) {

    public record RecommendedSpot(
            Long id,                    // 장소 ID
            String image,                   // 장소 이미지 URL
            Integer matchingRate,           // 취향 일치율 (Optional)
            String type,                    // 장소 분류
            String name,                    // 장소 이름
            Integer walkingTime             // 도보 시간
    ) {

    }
}
