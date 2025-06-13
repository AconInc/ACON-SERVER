package com.acon.server.spot.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Menu {

    private final Long id;
    private final Long spotId;
    private final String name;
    private final Integer price;

    @Builder
    public Menu(
            final Long id,
            final Long spotId,
            final String name,
            final Integer price
    ) {
        this.id = id;
        this.spotId = spotId;
        this.name = name;
        this.price = price;
    }
}
