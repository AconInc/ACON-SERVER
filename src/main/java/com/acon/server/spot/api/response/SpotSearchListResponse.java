package com.acon.server.spot.api.response;

import com.acon.server.spot.domain.enums.SpotType;
import java.util.List;
import lombok.Builder;

public record SpotSearchListResponse(
        List<SearchedSpot> spotList
) {

    @Builder
    public record SearchedSpot(
            Long spotId,
            String name,
            String address,
            SpotType spotType
    ) {

    }
}
