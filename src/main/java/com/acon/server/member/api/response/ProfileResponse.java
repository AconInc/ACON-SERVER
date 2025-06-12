package com.acon.server.member.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;

@Builder
@JsonInclude(Include.NON_NULL)
public record ProfileResponse(
        String profileImage,
        String nickname,
        String birthDate,
        List<SavedSpotResponse> savedSpotList
) {

}
