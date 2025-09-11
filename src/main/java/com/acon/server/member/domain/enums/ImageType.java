package com.acon.server.member.domain.enums;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ImageType {

    PROFILE(Arrays.asList("jpg", "jpeg", "png", "webp", "heic")),
    SPOT(Arrays.asList("jpg", "jpeg", "webp", "heic")),
    MENUBOARD(Arrays.asList("jpg", "jpeg", "png", "webp", "heic"));

    private final List<String> allowedExtensions;

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

    public boolean isAllowedExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return allowedExtensions.contains(extension);
    }

    public String getContentType(String fileName) {
        if (!isAllowedExtension(fileName)) {
            throw new BusinessException(ErrorType.INVALID_IMAGE_TYPE_ERROR);
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "heic" -> "image/heic";
            default -> "application/octet-stream";
        };
    }
}
