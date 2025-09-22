package com.acon.server.admin.infra.repository;

import com.acon.server.admin.domain.enums.MissingField;
import com.acon.server.admin.domain.enums.QueryTarget;
import com.acon.server.admin.infra.entity.QAdminEntity;
import com.acon.server.member.infra.entity.QMemberEntity;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.infra.entity.QMenuEntity;
import com.acon.server.spot.infra.entity.QMenuboardImageEntity;
import com.acon.server.spot.infra.entity.QOpeningHourEntity;
import com.acon.server.spot.infra.entity.QSpotEntity;
import com.acon.server.spot.infra.entity.QSpotImageEntity;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
// TODO: pagination 적용
public class AdminSpotRepository {

    private final JPAQueryFactory queryFactory;

    public List<SpotEntity> findSpotsByFilters(
            String query,
            QueryTarget queryTarget,
            List<SpotStatus> spotStatusList,
            MissingField missingField
    ) {
        QSpotEntity spot = QSpotEntity.spotEntity;
        QAdminEntity admin = QAdminEntity.adminEntity;
        QMemberEntity member = QMemberEntity.memberEntity;
        QOpeningHourEntity openingHour = QOpeningHourEntity.openingHourEntity;
        QMenuEntity menu = QMenuEntity.menuEntity;
        QMenuboardImageEntity menuboardImage = QMenuboardImageEntity.menuboardImageEntity;
        QSpotImageEntity spotImage = QSpotImageEntity.spotImageEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 상태 필터
        if (spotStatusList != null && !spotStatusList.isEmpty()) {
            builder.and(spot.spotStatus.in(spotStatusList));
        }

        // 검색어 필터
        if (query != null && !query.isBlank() && queryTarget != null) {
            switch (queryTarget) {
                case SPOT_ID -> {
                    try {
                        Long spotId = Long.parseLong(query);
                        builder.and(spot.id.eq(spotId));
                    } catch (NumberFormatException e) {
                        // ID 파싱 실패 시 검색 결과 없음
                        builder.and(spot.id.isNull().and(spot.id.isNotNull())); // 결과 없게 함 (항상 false)
                    }
                }
                case SPOT_NAME -> builder.and(spot.name.containsIgnoreCase(query));
                case USER_NICKNAME -> {
                    // appliedUserId를 통해 Member 또는 Admin 검색
                    BooleanBuilder userSearchBuilder = new BooleanBuilder();

                    // 1. Member에서 nickname으로 검색
                    List<Long> memberIds = queryFactory
                            .select(member.id)
                            .from(member)
                            .where(member.nickname.containsIgnoreCase(query))
                            .fetch();

                    // 2. Admin에서 username으로 검색
                    List<Long> adminIds = queryFactory
                            .select(admin.id)
                            .from(admin)
                            .where(admin.username.containsIgnoreCase(query))
                            .fetch();

                    // Member가 신청한 장소들
                    if (!memberIds.isEmpty()) {
                        userSearchBuilder.or(
                                spot.appliedUserId.in(memberIds)
                                        .and(spot.appliedByMember.eq(true))
                        );
                    }

                    // Admin이 등록한 장소들
                    if (!adminIds.isEmpty()) {
                        userSearchBuilder.or(
                                spot.appliedUserId.in(adminIds)
                                        .and(spot.appliedByMember.eq(false))
                        );
                    }

                    // appliedUserId가 null인 경우 (ADMIN으로 표시되는 케이스)
                    if (query.toUpperCase().contains("ADMIN")) {
                        userSearchBuilder.or(spot.appliedUserId.isNull());
                    }

                    // 검색 결과가 하나도 없으면
                    if (memberIds.isEmpty() && adminIds.isEmpty() && !query.toUpperCase().contains("ADMIN")) {
                        builder.and(spot.id.isNull().and(spot.id.isNotNull())); // 결과 없게 함
                    } else {
                        builder.and(userSearchBuilder);
                    }
                }
            }
        }

        // 누락 필드 필터
        if (missingField != null) {
            switch (missingField) {
                case SPOT_IMAGE -> {
                    builder.and(
                            JPAExpressions.selectOne()
                                    .from(spotImage)
                                    .where(spotImage.spotId.eq(spot.id))
                                    .notExists()
                    );
                }
                case OPENING_HOURS -> {
                    builder.and(
                            JPAExpressions.selectOne()
                                    .from(openingHour)
                                    .where(openingHour.spotId.eq(spot.id))
                                    .notExists()
                    );
                }
                case ALL -> {
                    // 장소 이미지, 영업시간, 메뉴판 이미지, 메뉴 중 하나라도 없는 경우
                    BooleanBuilder missingFieldBuilder = new BooleanBuilder();

                    // 장소 이미지 없는 경우
                    missingFieldBuilder.or(
                            JPAExpressions.selectOne()
                                    .from(spotImage)
                                    .where(spotImage.spotId.eq(spot.id))
                                    .notExists()
                    );

                    // 영업시간 없는 경우
                    missingFieldBuilder.or(
                            JPAExpressions.selectOne()
                                    .from(openingHour)
                                    .where(openingHour.spotId.eq(spot.id))
                                    .notExists()
                    );

                    // 메뉴판 이미지 없는 경우
                    missingFieldBuilder.or(
                            JPAExpressions.selectOne()
                                    .from(menuboardImage)
                                    .where(menuboardImage.spotId.eq(spot.id))
                                    .notExists()
                    );

                    // 메뉴 없는 경우
                    missingFieldBuilder.or(
                            JPAExpressions.selectOne()
                                    .from(menu)
                                    .where(menu.spotId.eq(spot.id))
                                    .notExists()
                    );

                    builder.and(missingFieldBuilder);
                }
            }
        }

        return queryFactory
                .selectFrom(spot)
                .where(builder)
                .orderBy(spot.updatedAt.desc())
                .fetch();
    }
}
