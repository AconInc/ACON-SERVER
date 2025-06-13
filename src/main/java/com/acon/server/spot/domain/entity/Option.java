package com.acon.server.spot.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Option {

    private final Long id;
    private final Long categoryId;
    private final String name;

    @Builder
    public Option(
            final Long id,
            final Long categoryId,
            final String name
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
    }
}
