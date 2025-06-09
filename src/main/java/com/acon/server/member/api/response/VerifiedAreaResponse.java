package com.acon.server.member.api.response;

public record VerifiedAreaResponse(
        Long verifiedAreaId,
        String name
) {

    public static VerifiedAreaResponse of(final Long verifiedAreaId, final String name) {
        return new VerifiedAreaResponse(verifiedAreaId, name);
    }
}
