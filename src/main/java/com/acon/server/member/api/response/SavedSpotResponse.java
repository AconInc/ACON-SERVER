package com.acon.server.member.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record SavedSpotResponse(
        Long spotId,
        String image,
        String name
) {

}
