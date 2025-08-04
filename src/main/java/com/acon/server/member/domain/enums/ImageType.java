package com.acon.server.member.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ImageType {

    PROFILE,
    SPOT,
    MENUBOARD,
    ;

    private static final Map<String, ImageType> IMAGE_TYPE_MAP = new HashMap<>();

    static {
        for (ImageType imageType : ImageType.values()) {
            IMAGE_TYPE_MAP.put(imageType.name(), imageType);
        }
    }

    public static ImageType fromValue(String value) {
        ImageType imageType = IMAGE_TYPE_MAP.get(value.toUpperCase());

        if (imageType == null) {
            throw new BusinessException(ErrorType.INVALID_IMAGE_TYPE_ERROR);
        }

        return imageType;
    }
}
