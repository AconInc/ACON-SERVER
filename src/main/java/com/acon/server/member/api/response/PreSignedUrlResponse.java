package com.acon.server.member.api.response;

public record PreSignedUrlResponse(
        String fileUrl,
        String preSignedUrl
) {

    public static PreSignedUrlResponse of(String fileUrl, String preSignedUrl) {
        return new PreSignedUrlResponse(fileUrl, preSignedUrl);
    }
}
