package com.acon.server.appintoss.application.service;

import com.acon.server.appintoss.api.request.AppInTossSpotRequest;
import com.acon.server.appintoss.api.response.AppInTossSpotResponse;
import com.acon.server.appintoss.infra.entity.AppInTossSpotEntity;
import com.acon.server.appintoss.infra.external.openai.OpenAIClient;
import com.acon.server.appintoss.infra.repository.AppInTossSpotRepository;
import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppInTossService {

    private final OpenAIClient openAIClient;
    private final AppInTossSpotRepository appInTossSpotRepository;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<AppInTossSpotResponse> postSpot(final AppInTossSpotRequest request) {
        log.info("장소 추천 요청 시작: userInput={}", request.userInput());

        return openAIClient.getSpotRecommendation(request.userInput())
                .toFuture()
                .handle((response, throwable) -> {
                    // 에러 케이스: API 호출 실패 (타임아웃, 네트워크 오류 등)
                    if (throwable != null) {
                        log.error("장소 추천 중 에러 발생: userInput={}", request.userInput(), throwable);
                        throw new BusinessException(ErrorType.APP_IN_TOSS_SPOT_NOT_FOUND_ERROR);
                    }

                    // 에러 케이스: 응답 검증 실패
                    if (response == null || response.success() == null || !response.success()) {
                        log.warn("OpenAI API가 장소를 찾지 못함: userInput={}", request.userInput());
                        throw new BusinessException(ErrorType.APP_IN_TOSS_SPOT_NOT_FOUND_ERROR);
                    }

                    // 성공 케이스
                    log.info("장소 추천 성공: spotName={}, category={}", response.spotName(), response.category());

                    // AppInTossSpotEntity 생성 및 저장
                    AppInTossSpotEntity entity = AppInTossSpotEntity.builder()
                            .name(response.spotName())
                            .category(response.category())
                            .build();

                    AppInTossSpotEntity savedEntity = appInTossSpotRepository.save(entity);

                    return AppInTossSpotResponse.of(savedEntity.getId(), response.spotName(), response.category());
                });
    }
}
