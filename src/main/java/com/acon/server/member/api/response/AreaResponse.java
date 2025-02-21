package com.acon.server.member.api.response;

public record AreaResponse(
        String area
) {

    public static AreaResponse of(String area) {
        return new AreaResponse(area);
    }
}
