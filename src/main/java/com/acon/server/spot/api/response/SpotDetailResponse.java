package com.acon.server.spot.api.response;

import com.acon.server.spot.domain.enums.SpotType;
import java.util.List;

public record SpotDetailResponse(
        Long spotId,
        String name,
        SpotType spotType,
        List<String> imageList,
        Boolean openStatus,
        String address,
        Integer localAcornCount,
        Integer basicAcornCount,
        Double latitude,
        Double longitude
) {

}
