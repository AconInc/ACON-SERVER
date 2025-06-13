package com.acon.server.member.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import lombok.Builder;

@Builder // TODO: 추후 Builder 제거하고 of로 대체 (record랑 같이 쓰는 거 X)
@JsonInclude(Include.NON_NULL)
public record ProfileResponse(
        String profileImage,
        String nickname,
        String birthDate,
        List<SavedSpotResponse> savedSpotList
) {

}
