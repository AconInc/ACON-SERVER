package com.acon.server.member.api.response;

public record AcornCountResponse(
        Integer acornCount
) {

    public static AcornCountResponse of(final Integer acornCount) {
        return new AcornCountResponse(acornCount);
    }
}
