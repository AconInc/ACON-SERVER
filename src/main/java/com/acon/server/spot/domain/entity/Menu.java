package com.acon.server.spot.domain.entity;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Menu {

    private final Long id;
    private final Long spotId;

    private String image;
    private String name;
    private int price;
    private boolean mainMenu;

    @Builder
    public Menu(
            Long id,
            Long spotId,
            String image,
            String name,
            int price,
            boolean mainMenu
    ) {
        this.id = id;
        this.spotId = spotId;
        this.image = image;
        this.name = name;
        this.price = price;
        this.mainMenu = mainMenu;
    }
}
