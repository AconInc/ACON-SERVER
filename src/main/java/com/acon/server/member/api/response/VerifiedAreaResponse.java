package com.acon.server.member.api.response;

import com.acon.server.member.infra.entity.VerifiedAreaEntity;

public record VerifiedAreaResponse(
        Long verifiedAreaId,
        String name
) {

    public static VerifiedAreaResponse of(final VerifiedAreaEntity verifiedAreaEntity) {
        return new VerifiedAreaResponse(verifiedAreaEntity.getId(), verifiedAreaEntity.getName());
    }
}
