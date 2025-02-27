package com.acon.server.global.scheduler;

import com.acon.server.spot.application.service.SpotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpotScheduler {

    private final SpotService spotService;

    // 매 시 정각에 실행
    @Scheduled(cron = "0 0 * * * *")
    public void scheduleUpdateCoordinates() {
        log.info("[스케줄링 작업] 위치 정보가 비어 있는 Spot 데이터 업데이트 시작");
        spotService.updateNullCoordinatesForSpots();
        log.info("[스케줄링 작업] Spot 데이터 업데이트 완료");
    }
}
