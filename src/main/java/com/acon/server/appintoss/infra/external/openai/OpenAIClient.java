package com.acon.server.appintoss.infra.external.openai;

import com.acon.server.appintoss.infra.external.openai.dto.OpenAIPromptRequest;
import com.acon.server.appintoss.infra.external.openai.dto.OpenAIPromptResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIClient {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/responses";
    private static final Duration TIMEOUT = Duration.ofSeconds(35);

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.prompt.id}")
    private String promptId;

    @Value("${openai.prompt.version}")
    private String promptVersion;

    private final WebClient webClient;

    public Mono<OpenAIPromptResponse> getSpotRecommendation(String userInput) {
        log.info("OpenAI API 호출 시작: userInput={}", userInput);

        return webClient.post()
                .uri(OPENAI_API_URL)
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(OpenAIPromptRequest.of(promptId, promptVersion, userInput))
                .retrieve()
                .bodyToMono(String.class)  // 먼저 String으로 받아서 로그 출력
                .doOnSuccess(rawResponse -> log.info("OpenAI API 원본 응답: {}", rawResponse))
                .map(rawResponse -> {
                    try {
                        // JSON 파싱 시도
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        return mapper.readValue(rawResponse, OpenAIPromptResponse.class);
                    } catch (Exception e) {
                        log.error("JSON 파싱 실패: {}", rawResponse, e);
                        throw new RuntimeException("JSON 파싱 실패", e);
                    }
                })
                .timeout(TIMEOUT)
                .doOnSuccess(response -> log.info("OpenAI API 호출 성공: success={}, spotName={}",
                        response.success(), response.spotName()))
                .doOnError(error -> log.error("OpenAI API 호출 실패", error));
    }
}
