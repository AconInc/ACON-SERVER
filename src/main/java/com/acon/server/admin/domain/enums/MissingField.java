package com.acon.server.admin.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;

public enum MissingField {

    ALL,
    SPOT_IMAGE,
    OPENING_HOURS,
    ;

    private static final Map<String, MissingField> MISSING_FIELD_MAP = new HashMap<>();

    static {
        for (MissingField missingField : MissingField.values()) {
            MISSING_FIELD_MAP.put(missingField.name(), missingField);
        }
    }

    @JsonCreator
    public static MissingField fromValue(String value) {
        MissingField missingField = MISSING_FIELD_MAP.get(value.toUpperCase());

        if (missingField == null) {
            throw new BusinessException(ErrorType.INVALID_MISSING_FIELD_ERROR);
        }

        return missingField;
    }
}
