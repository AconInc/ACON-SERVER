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
public enum SpotStatus {

    PENDING,
    ACTIVE,
    INACTIVE,
    DISCARDED,
    ;

    private static final Map<String, SpotStatus> SPOT_STATUS_MAP = new HashMap<>();

    static {
        for (SpotStatus status : SpotStatus.values()) {
            SPOT_STATUS_MAP.put(status.name(), status);
        }
    }

    @JsonCreator
    public static SpotStatus fromValue(String value) {
        SpotStatus spotStatus = SPOT_STATUS_MAP.get(value.toUpperCase());

        if (spotStatus == null) {
            throw new BusinessException(ErrorType.INVALID_SPOT_STATUS_ERROR);
        }

        return spotStatus;
    }
}
