package com.acon.server.appintoss.infra.external.openai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OpenAIPromptRequest(
        PromptInfo prompt
) {

    public static OpenAIPromptRequest of(String promptId, String promptVersion, String userInput) {
        return new OpenAIPromptRequest(
                new PromptInfo(
                        promptId,
                        promptVersion,
                        new Variables(userInput)
                )
        );
    }

    public record PromptInfo(
            String id,
            String version,
            Variables variables
    ) {

    }

    public record Variables(
            @JsonProperty("user_input")
            String userInput
    ) {

    }
}
