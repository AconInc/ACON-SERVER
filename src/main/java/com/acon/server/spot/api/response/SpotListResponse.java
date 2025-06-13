package com.acon.server.spot.api.response;

import com.acon.server.spot.domain.enums.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record SpotListResponse(
        String transportMode,
        List<RecommendedSpot> spotList
) {

    public record RecommendedSpot(
            Long spotId,
            String image,
            String name,
            Integer acornCount,
            List<Tag> tagList,
            Boolean isOpen,
            String closingTime,
            String nextOpening,
            Integer eta,
            Double latitude,
            Double longitude
    ) {

    }
}
