package com.acon.server.spot.api.response;

public record ReviewAvailabilityResponse(
        Boolean success
) {

    public static ReviewAvailabilityResponse of(boolean success) {
        return new ReviewAvailabilityResponse(success);
    }
}
