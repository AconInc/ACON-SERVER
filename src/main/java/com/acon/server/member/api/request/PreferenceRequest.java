package com.acon.server.member.api.request;

import com.acon.server.member.domain.enums.DislikeFood;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;

public record PreferenceRequest(
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        List<DislikeFood> dislikeFoodList
) {

}
