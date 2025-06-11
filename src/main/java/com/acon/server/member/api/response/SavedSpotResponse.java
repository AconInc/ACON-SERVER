package com.acon.server.member.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public record SavedSpotResponse(
        Long spotId,
        String image,
        String name
) {

    public static SavedSpotResponse of(
            final Long spotId,
            final String image,
            final String name
    ) {
        return new SavedSpotResponse(spotId, image, name);
    }
}
