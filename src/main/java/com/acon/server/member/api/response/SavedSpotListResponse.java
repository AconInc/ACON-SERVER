package com.acon.server.member.api.response;

import java.util.List;

public record SavedSpotListResponse(
        List<SavedSpotResponse> savedSpotList
) {

    public static SavedSpotListResponse of(final List<SavedSpotResponse> savedSpotList) {
        return new SavedSpotListResponse(savedSpotList);
    }
}
