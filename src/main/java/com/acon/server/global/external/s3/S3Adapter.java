package com.acon.server.global.external.s3;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// TODO: 추후 AWS SDK v2를 사용하는 방식으로 변경
public class S3Adapter {

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.s3.bucket.url-prefix}")
    private String bucketUrlPrefix;

    @Value("${cloud.aws.s3.path.profile-image}")
    private String profileImagePath;

    @Getter
    @Value("${cloud.aws.s3.path.basic-profile-image-url}")
    private String basicProfileImageUrl;

    @Value("${cloud.aws.s3.path.review-image}")
    private String reviewImagePath;

    @Value("${cloud.aws.s3.path.spot-image}")
    private String spotImagePath;

    private final AmazonS3 amazonS3;

    public String getPreSignedUrlForProfileImage(String fileName) {
        return getPreSignedUrl(profileImagePath, fileName);
    }

    // TODO: spotId 별로 directory 구분하도록 path 수정
    public String getPreSignedUrlForReviewImage(String fileName) {
        return getPreSignedUrl(reviewImagePath, fileName);
    }

    public String getPreSignedUrlForSpotImage(String fileName) {
        return getPreSignedUrl(spotImagePath, fileName);
    }

    private String getPreSignedUrl(String path, String fileName) {
        if (path != null && !path.isEmpty()) {
            fileName = path + fileName;
        }

        try {
            GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(fileName);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            return url.toString();
        } catch (Exception e) {
            throw new BusinessException(ErrorType.FAILED_GET_PRE_SIGNED_URL_ERROR);
        }
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpTime());

        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );

        return generatePresignedUrlRequest;
    }

    private Date getPreSignedUrlExpTime() {
        // TODO: 매직 넘버 yml 처리
        Instant expirationTime = Instant.now().plus(Duration.ofMinutes(2));

        return Date.from(expirationTime);
    }

    public String getProfileImageUrl(String fileName) {
        return getImageUrl(profileImagePath, fileName);
    }

    private String getImageUrl(String path, String fileName) {
        return amazonS3.getUrl(bucketName, path + fileName).toString();
    }

    public void validateProfileImageExists(String fileName) {
        validateImageExists(profileImagePath, fileName);
    }

    private void validateImageExists(String path, String fileName) {
        if (!amazonS3.doesObjectExist(bucketName, path + fileName)) {
            throw new BusinessException(ErrorType.INVALID_IMAGE_PATH_ERROR);
        }
    }

    public void deleteFile(String fileUrl) {
        if (!fileUrl.equals(basicProfileImageUrl)) {
            amazonS3.deleteObject(bucketName, getFileKey(fileUrl));
        }
    }

    private String getFileKey(String fileUrl) {
        return fileUrl.substring(bucketUrlPrefix.length());
    }
}
