package com.acon.server.admin.application.service;

import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.admin.api.response.SpotListResponse;
import com.acon.server.admin.api.response.SpotListResponse.SpotItem;
import com.acon.server.admin.domain.enums.MissingField;
import com.acon.server.admin.domain.enums.QueryTarget;
import com.acon.server.admin.infra.entity.AdminEntity;
import com.acon.server.admin.infra.repository.AdminRepository;
import com.acon.server.admin.infra.repository.AdminSpotRepository;
import com.acon.server.member.infra.entity.MemberEntity;
import com.acon.server.member.infra.repository.MemberRepository;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.infra.entity.SpotEntity;
import com.acon.server.spot.infra.repository.SpotRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SpotRepository spotRepository;
    private final MemberRepository memberRepository;
    private final AdminRepository adminRepository;
    private final AdminSpotRepository adminSpotRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        int pendingSpotCount = (int) spotRepository.countBySpotStatus(SpotStatus.PENDING);

        return DashboardResponse.of(pendingSpotCount);
    }

    @Transactional(readOnly = true)
    public SpotListResponse getSpots(
            String query,
            QueryTarget queryTarget,
            List<SpotStatus> spotStatusList,
            MissingField missingField
    ) {
        List<SpotEntity> spotEntityList = adminSpotRepository.findSpotsByFilters(
                query,
                queryTarget,
                spotStatusList,
                missingField
        );

        List<SpotItem> spotItemList = spotEntityList.stream()
                .map(spotEntity -> {
                    String userNickname;

                    if (spotEntity.getAppliedUserId() == null) {
                        userNickname = "ADMIN";
                    } else if (Boolean.TRUE.equals(spotEntity.getAppliedByMember())) {
                        MemberEntity memberEntity = memberRepository.findById(spotEntity.getAppliedUserId())
                                .orElse(null);
                        userNickname = memberEntity != null ? memberEntity.getNickname() : "탈퇴한 유저";
                    } else {
                        AdminEntity adminEntity = adminRepository.findById(spotEntity.getAppliedUserId())
                                .orElse(null);
                        userNickname = adminEntity != null ? adminEntity.getUsername() + "(ADMIN)" : "탈퇴한 어드민";
                    }

                    return SpotItem.of(
                            spotEntity.getId(),
                            userNickname,
                            spotEntity.getName(),
                            spotEntity.getSpotStatus().name(),
                            spotEntity.getSpotType().name(),
                            spotEntity.getUpdatedAt()
                    );
                })
                .toList();

        return SpotListResponse.of(spotItemList);
    }
}
