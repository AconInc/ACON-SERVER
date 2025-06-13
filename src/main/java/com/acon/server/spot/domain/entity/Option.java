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
            Long id,
            Long categoryId,
            String name
    ) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
    }
}
