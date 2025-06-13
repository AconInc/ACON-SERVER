package com.acon.server.member.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DislikeFood {

    SHRIMP,
    CRAB,
    CLAM,
    OYSTER,
    SASHIMI,
    FISH,
    SEAFOOD,
    YUKHOE_YUKSASHIMI,
    SEONJI,
    SUNDAE,
    GOPCHANG_DAECHANG_MAKCHANG,
    DAKBAL,
    DAKTTONGJIP,
    LAMB,
    OFFAL,
    VEGETABLE,
    ;

    private static final Map<String, DislikeFood> DISLIKE_FOOD_MAP = new HashMap<>();

    static {
        for (DislikeFood dislikeFood : DislikeFood.values()) {
            DISLIKE_FOOD_MAP.put(dislikeFood.name(), dislikeFood);
        }
    }

    public static DislikeFood fromValue(String value) {
        DislikeFood dislikeFood = DISLIKE_FOOD_MAP.get(value.toUpperCase());

        if (dislikeFood == null) {
            throw new BusinessException(ErrorType.INVALID_DISLIKE_FOOD_ERROR);
        }

        return dislikeFood;
    }
}
