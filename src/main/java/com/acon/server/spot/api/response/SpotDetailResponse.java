package com.acon.server.spot.api.response;

import com.acon.server.spot.domain.enums.Tag;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public record SpotDetailResponse(
        Long spotId,
        List<String> imageList,
        String name,
        Integer acornCount,
        List<Tag> tagList,
        Boolean isOpen,
        String closingTime,
        String nextOpening,
        Boolean hasMenuboardImage,
        Boolean isSaved,
        List<MenuResponse> signatureMenuList,
        Double latitude,
        Double longitude
) {

}
