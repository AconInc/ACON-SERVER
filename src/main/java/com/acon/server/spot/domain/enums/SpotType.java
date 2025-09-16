package com.acon.server.spot.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SpotType {

    RESTAURANT,
    CAFE,
    ;

    private static final Map<String, SpotType> SPOT_TYPE_MAP = new HashMap<>();

    static {
        for (SpotType spotType : SpotType.values()) {
            SPOT_TYPE_MAP.put(spotType.name(), spotType);
        }
    }

    @JsonCreator
    public static SpotType fromValue(String value) {
        SpotType spotType = SPOT_TYPE_MAP.get(value.toUpperCase());

        if (spotType == null) {
            throw new BusinessException(ErrorType.INVALID_SPOT_TYPE_ERROR);
        }

        return spotType;
    }
}
