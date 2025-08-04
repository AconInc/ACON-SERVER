package com.acon.server.spot.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SpotApplicationStatus {

    PENDING,
    APPROVED,
    REJECTED,
    ;

    private static final Map<String, SpotApplicationStatus> SPOT_APPLICATION_STATUS_MAP = new HashMap<>();

    static {
        for (SpotApplicationStatus status : SpotApplicationStatus.values()) {
            SPOT_APPLICATION_STATUS_MAP.put(status.name(), status);
        }
    }

    public static SpotApplicationStatus fromValue(String value) {
        SpotApplicationStatus spotApplicationStatus = SPOT_APPLICATION_STATUS_MAP.get(value.toUpperCase());

        if (spotApplicationStatus == null) {
            throw new BusinessException(ErrorType.INVALID_SPOT_APPLICATION_STATUS_ERROR);
        }

        return spotApplicationStatus;
    }
}
