package com.acon.server.member.infra.entity;

import com.acon.server.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "withdrawal_reason")
public class WithdrawalReasonEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Builder
    public WithdrawalReasonEntity(
            Long id,
            String reason
    ) {
        this.id = id;
        this.reason = reason;
    }
}
