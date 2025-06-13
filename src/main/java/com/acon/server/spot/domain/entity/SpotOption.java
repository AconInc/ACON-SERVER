package com.acon.server.spot.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SpotOption {

    private final Long id;
    private final Long spotId;
    private final Long optionId;

    @Builder
    public SpotOption(
            final Long id,
            final Long spotId,
            final Long optionId
    ) {
        this.id = id;
        this.spotId = spotId;
        this.optionId = optionId;
    }
}
