package com.acon.server.admin.application.service;

import com.acon.server.admin.api.response.DashboardResponse;
import com.acon.server.spot.domain.enums.SpotStatus;
import com.acon.server.spot.infra.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final SpotRepository spotRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        int pendingSpotCount = (int) spotRepository.countBySpotStatus(SpotStatus.PENDING);

        return DashboardResponse.of(pendingSpotCount);
    }
}
