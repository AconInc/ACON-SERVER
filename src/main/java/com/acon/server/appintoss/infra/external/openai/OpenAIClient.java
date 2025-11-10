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
                .bodyToMono(OpenAIPromptResponse.class)
                .timeout(TIMEOUT)
                .doOnSuccess(response -> log.info("OpenAI API 호출 성공: success={}, spotName={}",
                        response.success(), response.spotName()))
                .doOnError(error -> log.error("OpenAI API 호출 실패", error));
    }
}
