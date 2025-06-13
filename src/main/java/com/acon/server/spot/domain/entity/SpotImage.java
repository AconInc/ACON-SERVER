package com.acon.server.spot.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SpotImage {

    private final Long id;
    private final Long spotId;
    private final String image;

    @Builder
    public SpotImage(
            final Long id,
            final Long spotId,
            final String image
    ) {
        this.id = id;
        this.spotId = spotId;
        this.image = image;
    }
}
