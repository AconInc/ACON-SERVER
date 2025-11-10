package com.acon.server.appintoss.api.response;

public record AppInTossSpotResponse(
        Long id,
        String spotName,
        String category
) {

    public static AppInTossSpotResponse of(
            Long id,
            String spotName,
            String category
    ) {
        return new AppInTossSpotResponse(id, spotName, category);
    }
}
