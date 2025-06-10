package com.acon.server.member.infra.entity;

import com.acon.server.member.domain.enums.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: socialType, socialId를 unique로 묶기
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", nullable = false, unique = true)
    private String socialId;

    @Column(name = "external_uuid", nullable = false, unique = true)
    private String externalUUID;

    @Column(name = "recent_latitude")
    private Double recentLatitude;

    @Column(name = "recent_longitude")
    private Double recentLongitude;

    @Column(name = "profile_image", nullable = false)
    private String profileImage;

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "nickname_updated_at")
    private LocalDate nicknameUpdatedAt;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "left_acorn_count", nullable = false)
    private int leftAcornCount;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public MemberEntity(
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
            Integer leftAcornCount,
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
        // TODO: 도메인 로직으로 이동
        this.leftAcornCount = leftAcornCount != null ? leftAcornCount : 25;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
