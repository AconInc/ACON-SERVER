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
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "member",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_member_social_type_social_id",
                columnNames = {"social_type", "social_id"}
        )
)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", length = 10, nullable = false)
    private SocialType socialType;

    @Column(name = "social_id", length = 50, nullable = false, unique = true)
    private String socialId;

    @Column(name = "external_uuid", length = 50, nullable = false, unique = true)
    private String externalUUID;

    @Column(name = "profile_image", columnDefinition = "text", nullable = false)
    private String profileImage;

    @Column(name = "nickname", length = 20, nullable = false, unique = true)
    private String nickname;

    @Column(name = "nickname_updated_at", nullable = false)
    private LocalDateTime nicknameUpdatedAt;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "left_acorn_count", nullable = false)
    private Integer leftAcornCount;

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
            String profileImage,
            String nickname,
            LocalDateTime nicknameUpdatedAt,
            LocalDate birthDate,
            Integer leftAcornCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
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
}
