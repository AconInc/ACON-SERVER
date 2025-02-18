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
    EMPTY_PRINCIPAL_ERROR(HttpStatus.UNAUTHORIZED, 40102, "Principal 객체가 없습니다."),
    UN_LOGIN_ERROR(HttpStatus.UNAUTHORIZED, 40103, "로그인 후 진행해 주세요."),
    BEARER_LOST_ERROR(HttpStatus.UNAUTHORIZED, 40104, "요청한 토큰이 Bearer 토큰이 아닙니다."),

    /* 404 Not Found */
    NOT_FOUND_PATH_ERROR(HttpStatus.NOT_FOUND, 40401, "존재하지 않는 경로입니다."),
    NOT_FOUND_MEMBER_ERROR(HttpStatus.NOT_FOUND, 40402, "존재하지 않는 회원입니다."),

    /* 500 Internal Server Error */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "예상치 못한 서버 에러가 발생했습니다."),

    /* Member Error */
    /* 400 Bad Request */
    INVALID_SOCIAL_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40009, "유효하지 않은 socialType입니다."),
    INVALID_ID_TOKEN_ERROR(HttpStatus.BAD_REQUEST, 40010, "ID 토큰의 서명이 올바르지 않습니다."),
    INVALID_DISLIKE_FOOD_ERROR(HttpStatus.BAD_REQUEST, 40013, "유효하지 않은 dislikeFood입니다."),
    INVALID_CUISINE_ERROR(HttpStatus.BAD_REQUEST, 40014, "유효하지 않은 cuisine입니다."),
    INVALID_SPOT_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40015, "유효하지 않은 spotType입니다."),
    INVALID_SPOT_STYLE_ERROR(HttpStatus.BAD_REQUEST, 40016, "유효하지 않은 spotStyle입니다."),
    INVALID_FAVORITE_SPOT_ERROR(HttpStatus.BAD_REQUEST, 40017, "유효하지 않은 favoriteSpot입니다."),
    INVALID_FAVORITE_SPOT_RANK_SIZE_ERROR(HttpStatus.BAD_REQUEST, 40030, "favoriteSpotRank의 사이즈가 잘못되었습니다."),
    INVALID_FAVORITE_CUISINE_RANK_SIZE_ERROR(HttpStatus.BAD_REQUEST, 40031, "favoriteCuisineRank의 사이즈가 잘못되었습니다."),
    INVALID_AREA_SIZE_ERROR(HttpStatus.BAD_REQUEST, 40032, "동네 인증은 최소 1개 ~ 최대 5개까지 가능합니다."),
    INVALID_IMAGE_TYPE_ERROR(HttpStatus.BAD_REQUEST, 40045, "유효하지 않은 imageType입니다."),
    INVALID_NICKNAME_ERROR(HttpStatus.BAD_REQUEST, 40051, "닉네임이 조건을 만족하지 않습니다."),
    INVALID_BIRTH_DATE_ERROR(HttpStatus.BAD_REQUEST, 40053, "유효하지 않은 생년월일입니다."),
    INVALID_VERIFIED_AREA_ERROR(HttpStatus.BAD_REQUEST, 40054, "유효하지 않은 인증 동네입니다."),

    /* 404 Not Found */
    NOT_FOUND_VERIFIED_AREA_ERROR(HttpStatus.NOT_FOUND, 40404, "존재하지 않는 인증 동네입니다."),

    /* 409 Conflict */
    DUPLICATED_NICKNAME_ERROR(HttpStatus.CONFLICT, 40901, "이미 사용 중인 닉네임입니다."),

    /* 500 Internal Server Error */
    FAILED_DOWNLOAD_GOOGLE_PUBLIC_KEY_ERROR(HttpStatus.BAD_REQUEST, 50002, "구글 공개키 다운로드에 실패하였습니다."),
    FAILED_GET_PRE_SIGNED_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50005, "PreSigned URL 획득에 실패하였습니다."),

    /* Review Error */
    /* 400 Bad Request */
    INSUFFICIENT_ACORN_COUNT_ERROR(HttpStatus.BAD_REQUEST, 40098, "사용 가능한 도토리 개수가 부족합니다."),

    /* Spot Error */
    /* 400 Bad Request */
    INVALID_DAY_ERROR(HttpStatus.BAD_REQUEST, 40099, "유효하지 않은 day입니다."),

    /* 404 Not Found */
    NOT_FOUND_SPOT_ERROR(HttpStatus.NOT_FOUND, 40403, "존재하지 않는 장소입니다."),

    /* 500 Internal Server Error */
    NAVER_MAPS_GEOCODING_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 50003, "Naver Maps GeoCoding API 호출에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;
}
