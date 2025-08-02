package com.acon.server.review.api.response;

public record ReviewAvailabilityResponse(
        Boolean available
) {

    public static ReviewAvailabilityResponse of(boolean available) {
        return new ReviewAvailabilityResponse(available);
    }
}
