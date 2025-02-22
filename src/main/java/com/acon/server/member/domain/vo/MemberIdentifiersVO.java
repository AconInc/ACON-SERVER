package com.acon.server.member.domain.vo;

public record MemberIdentifiersVO(
        Long memberId,
        String externalUUID
) {

    public static MemberIdentifiersVO of(Long memberId, String externalUUID) {
        return new MemberIdentifiersVO(memberId, externalUUID);
    }
}
