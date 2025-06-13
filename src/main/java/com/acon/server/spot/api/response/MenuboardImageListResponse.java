package com.acon.server.spot.api.response;

import java.util.List;

public record MenuboardImageListResponse(
        List<String> menuboardImageList
) {

    public static MenuboardImageListResponse of(List<String> menuboardImageList) {
        return new MenuboardImageListResponse(menuboardImageList);
    }
}