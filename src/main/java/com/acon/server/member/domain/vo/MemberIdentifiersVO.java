package com.acon.server.member.domain.vo;

import com.acon.server.member.infra.entity.MemberEntity;

public record MemberIdentifiersVO(
        Long memberId,
        String externalUUID
) {

    public static MemberIdentifiersVO of(final MemberEntity memberEntity) {
        return new MemberIdentifiersVO(memberEntity.getId(), memberEntity.getExternalUUID());
    }
}
