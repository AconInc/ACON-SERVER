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
public enum Tag {

    NEW,
    LOCAL,
    TOP1,
    TOP2,
    TOP3,
    TOP4,
    TOP5,
    ;

    private static final Map<String, Tag> TAG_MAP = new HashMap<>();

    static {
        for (Tag tag : Tag.values()) {
            TAG_MAP.put(tag.name(), tag);
        }
    }

    public static Tag fromValue(String value) {
        Tag tag = TAG_MAP.get(value.toUpperCase());

        if (tag == null) {
            throw new BusinessException(ErrorType.INVALID_TAG_ERROR);
        }

        return tag;
    }
}
