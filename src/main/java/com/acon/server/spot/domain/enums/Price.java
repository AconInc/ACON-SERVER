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
public enum Price {

    VALUE_FOR_MONEY,
    ;

    private static final Map<String, Price> PRICE_MAP = new HashMap<>();

    static {
        for (Price price : Price.values()) {
            PRICE_MAP.put(price.name(), price);
        }
    }

    public static Price fromValue(String value) {
        Price price = PRICE_MAP.get(value.toUpperCase());

        if (price == null) {
            throw new BusinessException(ErrorType.INVALID_PRICE_ERROR);
        }

        return price;
    }
}
