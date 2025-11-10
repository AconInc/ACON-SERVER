package com.acon.server.appintoss.infra.external.openai.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIWrapperResponse(
        @JsonProperty("output")
        List<OutputItem> output
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OutputItem(
            @JsonProperty("type")
            String type,

            @JsonProperty("content")
            List<ContentItem> content
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ContentItem(
            @JsonProperty("type")
            String type,

            @JsonProperty("text")
            String text
    ) {
    }
}
