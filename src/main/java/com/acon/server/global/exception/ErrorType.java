package com.acon.server.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorType {

    // TODO: ErrorType code 한 번 싹 정리

    /* Common Error */
    /* 400 Bad Request */
    INVALID_PATH_ERROR(HttpStatus.BAD_REQUEST, 40001, "요청 경로의 변수 값이 허용된 형식과 다릅니다."),
    INVALID_FIELD_ERROR(HttpStatus.BAD_REQUEST, 40002, "요청 본문의 필드 값이 허용된 형식과 다릅니다."),
    NO_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, 40003, "요청에 필요한 파라미터가 존재하지 않습니다."),
    NO_REQUEST_HEADER_ERROR(HttpStatus.BAD_REQUEST, 40004, "요청에 필요한 헤더가 존재하지 않습니다."),
    TYPE_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, 40005, "유효하지 않은 값이 입력되었습니다."),
    INVALID_REQUEST_BODY_ERROR(HttpStatus.BAD_REQUEST, 40006, "유효하지 않은 Request Body입니다. 요청 형식 또는 필드를 확인하세요."),
    DATA_INTEGRITY_VIOLATION_ERROR(HttpStatus.BAD_REQUEST, 40007, "데이터 무결성 제약 조건을 위반했습니다."),
    INVALID_ACCESS_TOKEN_ERROR(HttpStatus.BAD_REQUEST, 40008, "유효하지 않은 accessToken입니다."),
    INVALID_REFRESH_TOKEN_ERROR(HttpStatus.BAD_REQUEST, 40088, "유효하지 않은 refreshToken입니다."),
    INVALID_IMAGE_PATH_ERROR(HttpStatus.NOT_FOUND, 40052, "유효하지 않은 이미지 경로입니다."),

    /* 401 Unauthorized */
    EXPIRED_ACCESS_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, 40101, "만료된 accessToken입니다."),
    NO_PRINCIPAL_ERROR(HttpStatus.UNAUTHORIZED, 40102, "Principal 객체가 없습니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, 40103, "접근 권한이 없습니다. 로그인 후 이용해 주세요."),
    BEARER_LOST_ERROR(HttpStatus.UNAUTHORIZED, 40104, "요청한 토큰이 Bearer 토큰이 아닙니다."),

    /* 404 Not Found */
    NOT_FOUND_PATH_ERROR(HttpStatus.NOT_FOUND, 40401, "존재하지 않는 경로입니다."),
    NOT_FOUND_MEMBER_ERROR(HttpStatus.NOT_FOUND, 40402, "존재하지 않는 회원입니다."),

    /* 500 Internal Server Error */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "예상치 못한 서버 에러가 발생했습니다."),

    /* Admin Error */
    /* 401 Unauthorized */
    INVALID_ID_OR_PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, 40105, "아이디 또는 비밀번호가 일치하지 않습니다."),

    /* 403 Forbidden */
    ACCESS_DENIED_ERROR(HttpStatus.FORBIDDEN, 40301, "권한이 없거나 보안 정책에 의해 요청이 차단되었습니다."),
    MISSING_CSRF_TOKEN_ERROR(HttpStatus.FORBIDDEN, 40302, "CSRF 토큰이 누락되었습니다."),
    INVALID_CSRF_TOKEN_ERROR(HttpStatus.FORBIDDEN, 40303, "유효하지 않은 CSRF 토큰입니다."),

    /* Member Error */
    /* 400 Bad Request */
    INVALID_SOCIAL_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40009, "유효하지 않은 socialType입니다."),
    INVALID_ID_TOKEN_ERROR(HttpStatus.BAD_REQUEST, 40010, "ID 토큰의 서명이 올바르지 않습니다."),
    INVALID_DISLIKE_FOOD_ERROR(HttpStatus.BAD_REQUEST, 40013, "유효하지 않은 dislikeFood입니다."),
    INVALID_VERIFIED_AREA_COUNT_ERROR(HttpStatus.BAD_REQUEST, 40032, "인증 지역은 최소 1개 ~ 최대 3개까지 가능합니다."),
    INVALID_IMAGE_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40045, "유효하지 않은 imageType입니다."),
    INVALID_PLATFORM_ERROR(HttpStatus.BAD_REQUEST, 40046, "유효하지 않은 platform입니다."),
    INVALID_NICKNAME_ERROR(HttpStatus.BAD_REQUEST, 40051, "닉네임이 조건을 만족하지 않습니다."),
    INVALID_BIRTH_DATE_ERROR(HttpStatus.BAD_REQUEST, 40053, "유효하지 않은 생년월일입니다."),
    INVALID_VERIFIED_AREA_ERROR(HttpStatus.BAD_REQUEST, 40054, "유효하지 않은 인증 지역입니다."),
    VERIFIED_AREA_DELETE_RESTRICTION_ERROR(HttpStatus.BAD_REQUEST, 40055, "인증일로부터 1주 이상 3개월 미만인 지역은 삭제할 수 없습니다."),
    VERIFIED_AREA_REPLACE_RESTRICTION_ERROR(HttpStatus.BAD_REQUEST, 40056, "인증 지역이 2개 이상인 경우 인증 지역을 교체할 수 없습니다."),

    /* 404 Not Found */
    NOT_FOUND_VERIFIED_AREA_ERROR(HttpStatus.NOT_FOUND, 40404, "존재하지 않는 인증 지역입니다."),

    /* 409 Conflict */
    DUPLICATE_MEMBER_ERROR(HttpStatus.CONFLICT, 40902, "이미 가입된 회원입니다."),
    DUPLICATE_NICKNAME_ERROR(HttpStatus.CONFLICT, 40901, "이미 사용 중인 닉네임입니다."),

    /* 500 Internal Server Error */
    FAILED_DOWNLOAD_GOOGLE_PUBLIC_KEY_ERROR(HttpStatus.BAD_REQUEST, 50002, "구글 공개키 다운로드에 실패하였습니다."),
    FAILED_GET_PRE_SIGNED_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50005, "PreSigned URL 획득에 실패하였습니다."),

    /* Review Error */
    /* 400 Bad Request */
    INSUFFICIENT_ACORN_COUNT_ERROR(HttpStatus.BAD_REQUEST, 40098, "사용 가능한 도토리 개수가 부족합니다."),

    /* Spot Error */
    /* 400 Bad Request */
    INVALID_SPOT_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40015, "유효하지 않은 spotType입니다."),
    INVALID_CATEGORY_NAME_ERROR(HttpStatus.BAD_REQUEST, 40016, "유효하지 않은 categoryName입니다."),
    INVALID_RESTAURANT_FEATURE_ERROR(HttpStatus.BAD_REQUEST, 40017, "유효하지 않은 restaurantFeature입니다."),
    INVALID_CAFE_FEATURE_ERROR(HttpStatus.BAD_REQUEST, 40018, "유효하지 않은 cafeFeature입니다."),
    INVALID_OPENING_HOUR_ERROR(HttpStatus.BAD_REQUEST, 40019, "유효하지 않은 openingHour입니다."),
    INVALID_PRICE_ERROR(HttpStatus.BAD_REQUEST, 40020, "유효하지 않은 price입니다."),
    INVALID_SPOT_TYPE_CATEGORY_ERROR(HttpStatus.BAD_REQUEST, 40021, "spotType에 해당하지 않는 카테고리입니다."),
    INVALID_CATEGORY_OPTION_ERROR(HttpStatus.BAD_REQUEST, 40022, "category에 해당하지 않는 option입니다."),
    INVALID_TAG_ERROR(HttpStatus.BAD_REQUEST, 40024, "유효하지 않은 tag입니다."),
    INVALID_SPOT_APPLICATION_STATUS_ERROR(HttpStatus.BAD_REQUEST, 40025, "유효하지 않은 spotApplicationStatus입니다."),

    /* 404 Not Found */
    NOT_FOUND_SPOT_ERROR(HttpStatus.NOT_FOUND, 40403, "존재하지 않는 장소입니다."),
    UNAVAILABLE_SERVICE_AREA_ERROR(HttpStatus.NOT_FOUND, 40405, "서비스를 제공하지 않는 지역입니다."),

    /* 500 Internal Server Error */
    NAVER_MAPS_GEOCODING_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "Naver Maps GeoCoding API 호출에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
