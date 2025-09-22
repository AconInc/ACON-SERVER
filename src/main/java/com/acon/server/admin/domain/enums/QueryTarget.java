package com.acon.server.admin.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.HashMap;
import java.util.Map;

public enum QueryTarget {

    SPOT_ID,
    USER_NICKNAME,
    SPOT_NAME,
    ;

    private static final Map<String, QueryTarget> QUERY_TARGET_MAP = new HashMap<>();

    static {
        for (QueryTarget queryTarget : QueryTarget.values()) {
            QUERY_TARGET_MAP.put(queryTarget.name(), queryTarget);
        }
    }

    public static QueryTarget fromValue(String value) {
        QueryTarget queryTarget = QUERY_TARGET_MAP.get(value.toUpperCase());

        if (queryTarget == null) {
            throw new BusinessException(ErrorType.INVALID_QUERY_TARGET_ERROR);
        }

        return queryTarget;
    }
}
