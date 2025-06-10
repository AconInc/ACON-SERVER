package com.acon.server.member.domain.entity;

import com.acon.server.member.domain.enums.SocialType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private final Long id;
    private final SocialType socialType;
    private final String socialId;
    private final String externalUUID;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private String profileImage;
    private String nickname;
    private LocalDateTime nicknameUpdatedAt;
    private LocalDate birthDate;
    private int leftAcornCount;

    @Builder
    public Member(
            final Long id,
            final SocialType socialType,
            final String socialId,
            final String externalUUID,
            final String profileImage,
            final String nickname,
            final LocalDateTime nicknameUpdatedAt,
            final LocalDate birthDate,
            final int leftAcornCount,
            final LocalDateTime createdAt,
            final LocalDateTime updatedAt
    ) {
        this.id = id;
        this.socialType = socialType;
        this.socialId = socialId;
        this.externalUUID = externalUUID;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.nicknameUpdatedAt = nicknameUpdatedAt;
        this.birthDate = birthDate;
        this.leftAcornCount = leftAcornCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setProfileImage(final String profileImage) {
        this.profileImage = profileImage;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
        this.nicknameUpdatedAt = LocalDateTime.now();
    }

    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // TODO: 에러 처리
    public void useAcorn(final int acornCount) {
        if (leftAcornCount >= acornCount) {
            this.leftAcornCount -= acornCount;
        }
    }
}
