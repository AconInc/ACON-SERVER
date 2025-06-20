package com.acon.server.member.infra.entity;

import com.acon.server.member.domain.enums.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "update_policy")
public class UpdatePolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 10, nullable = false)
    private Platform platform;

    @Column(name = "from_version", length = 20, nullable = false)
    private String fromVersion;

    @Column(name = "to_version", length = 20, nullable = false)
    private String toVersion;

    @Builder
    public UpdatePolicyEntity(
            Long id,
            Platform platform,
            String fromVersion,
            String toVersion
    ) {
        this.id = id;
        this.platform = platform;
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }
}
