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
public enum SocialType {

    GOOGLE,
    APPLE,
    ;

    private static final Map<String, SocialType> SOCIAL_TYPE_MAP = new HashMap<>();

    static {
        for (SocialType socialType : SocialType.values()) {
            SOCIAL_TYPE_MAP.put(socialType.name(), socialType);
        }
    }

    @JsonCreator
    public static SocialType fromValue(String value) {
        SocialType socialType = SOCIAL_TYPE_MAP.get(value.toUpperCase());

        if (socialType == null) {
            throw new BusinessException(ErrorType.INVALID_SOCIAL_TYPE_ERROR);
        }

        return socialType;
    }
}
