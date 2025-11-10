package com.acon.server.appintoss.infra.external.openai.dto;

public record OpenAIPromptResponse(
        Boolean success,
        String spotName,
        String category
) {

}
