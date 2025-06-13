package com.acon.server.spot.api.response;

public record MenuResponse(
        String name,
        Integer price
) {

    public static MenuResponse of(
            String name,
            Integer price
    ) {
        return new MenuResponse(name, price);
    }
}
