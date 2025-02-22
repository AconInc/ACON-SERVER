package com.acon.server.member.domain.entity;

import com.acon.server.member.domain.enums.SocialType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Member {

    private final Long id;
    private final SocialType socialType;
    private final String socialId;
    private final String externalUUID;

    private Double recentLatitude;
    private Double recentLongitude;
    private String profileImage;
    private String nickname;
    private LocalDate nicknameUpdatedAt;
    private LocalDate birthDate;
    private int leftAcornCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Member(
            Long id,
            SocialType socialType,
            String socialId,
            String externalUUID,
            Double recentLatitude,
            Double recentLongitude,
            String profileImage,
            String nickname,
            LocalDate nicknameUpdatedAt,
            LocalDate birthDate,
            int leftAcornCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.socialType = socialType;
        this.socialId = socialId;
        this.externalUUID = externalUUID;
        this.recentLatitude = recentLatitude;
        this.recentLongitude = recentLongitude;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.nicknameUpdatedAt = nicknameUpdatedAt;
        this.birthDate = birthDate;
        this.leftAcornCount = leftAcornCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.nicknameUpdatedAt = LocalDate.now();
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // TODO: 에러 처리
    public void useAcorn(int acornCount) {
        if (leftAcornCount >= acornCount) {
            this.leftAcornCount -= acornCount;
        }
    }
}
