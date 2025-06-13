package com.acon.server.spot.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public enum OpeningHour {

    OPEN_AFTER_10PM,
    ;

    private static final Map<String, OpeningHour> OPENING_HOUR_MAP = new HashMap<>();

    static {
        for (OpeningHour openingHour : OpeningHour.values()) {
            OPENING_HOUR_MAP.put(openingHour.name(), openingHour);
        }
    }

    public static OpeningHour fromValue(String value) {
        OpeningHour openingHour = OPENING_HOUR_MAP.get(value.toUpperCase());

        if (openingHour == null) {
            throw new BusinessException(ErrorType.INVALID_OPENING_HOUR_ERROR);
        }

        return openingHour;
    }
}
