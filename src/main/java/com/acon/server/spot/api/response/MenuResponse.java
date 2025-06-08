package com.acon.server.spot.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record MenuResponse(
        Long id,
        String name,
        Integer price,
        String image
) {

}
