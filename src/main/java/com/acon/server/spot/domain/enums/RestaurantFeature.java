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
public enum RestaurantFeature {

    KOREAN,
    CHINESE,
    JAPANESE,
    WESTERN,
    SOUTHEAST_ASIAN,
    FUSION,
    BUNSIK,
    BUFFET,
    DRINKING_PLACE,
    EXCLUDE_FRANCHISE,
    ;

    private static final Map<String, RestaurantFeature> RESTAURANT_FEATURE_MAP = new HashMap<>();

    static {
        for (RestaurantFeature restaurantFeature : RestaurantFeature.values()) {
            RESTAURANT_FEATURE_MAP.put(restaurantFeature.name(), restaurantFeature);
        }
    }

    public static RestaurantFeature fromValue(String value) {
        RestaurantFeature restaurantFeature = RESTAURANT_FEATURE_MAP.get(value.toUpperCase());

        if (restaurantFeature == null) {
            throw new BusinessException(ErrorType.INVALID_RESTAURANT_FEATHER_ERROR);
        }

        return restaurantFeature;
    }
}
