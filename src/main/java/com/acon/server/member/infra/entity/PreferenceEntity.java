package com.acon.server.member.infra.entity;

import com.acon.server.member.domain.enums.DislikeFood;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "preference")
public class PreferenceEntity {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dislike_food_list", nullable = false)
    private List<DislikeFood> dislikeFoodList;

    @Builder
    public PreferenceEntity(
            Long memberId,
            List<DislikeFood> dislikeFoodList
    ) {
        this.memberId = memberId;
        this.dislikeFoodList = dislikeFoodList;
    }
}
