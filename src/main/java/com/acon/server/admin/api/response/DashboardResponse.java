package com.acon.server.admin.api.response;

public record DashboardResponse(
        Integer pendingSpotCount
) {

    public static DashboardResponse of(
            Integer pendingSpotCount
    ) {
        return new DashboardResponse(pendingSpotCount);
    }
}
