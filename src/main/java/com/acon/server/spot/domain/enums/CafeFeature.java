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
public enum CafeFeature {

    WORK_FRIENDLY,
    ;

    private static final Map<String, CafeFeature> CAFE_FEATURE_MAP = new HashMap<>();

    static {
        for (CafeFeature cafeFeature : CafeFeature.values()) {
            CAFE_FEATURE_MAP.put(cafeFeature.name(), cafeFeature);
        }
    }

    public static CafeFeature fromValue(String value) {
        CafeFeature cafeFeature = CAFE_FEATURE_MAP.get(value.toUpperCase());

        if (cafeFeature == null) {
            throw new BusinessException(ErrorType.INVALID_CAFE_FEATURE_ERROR);
        }

        return cafeFeature;
    }
}
