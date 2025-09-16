package com.acon.server.member.domain.enums;

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
public enum Platform {

    ANDROID,
    IOS,
    ;

    private static final Map<String, Platform> PLATFORM_MAP = new HashMap<>();

    static {
        for (Platform platform : Platform.values()) {
            PLATFORM_MAP.put(platform.name(), platform);
        }
    }

    @JsonCreator
    public static Platform fromValue(String value) {
        Platform platform = PLATFORM_MAP.get(value.toUpperCase());

        if (platform == null) {
            throw new BusinessException(ErrorType.INVALID_PLATFORM_ERROR);
        }

        return platform;
    }
}
