package com.acon.server.global.external.s3;

import com.acon.server.global.exception.BusinessException;
import com.acon.server.global.exception.ErrorType;
import com.acon.server.member.domain.enums.ImageType;
import java.time.Duration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
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

    @Value("${cloud.aws.s3.path.temp-spot-image}")
    private String tempSpotImagePath;

    @Value("${cloud.aws.s3.path.temp-menuboard-image}")
    private String tempMenuBoardImagePath;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public String getPreSignedUrlForProfileImage(String fileName, String contentType) {
        return getPreSignedUrl(profileImagePath, fileName, contentType);
    }

    public String getPreSignedUrlForSpotImage(String fileName, String contentType) {
        return getPreSignedUrl(tempSpotImagePath, fileName, contentType);
    }

    public String getPreSignedUrlForMenuboardImage(String fileName, String contentType) {
        return getPreSignedUrl(tempMenuBoardImagePath, fileName, contentType);
    }

    private String getPreSignedUrl(String path, String fileName, String contentType) {
        if (StringUtils.hasText(path)) {
            fileName = path + fileName;
        }

        try {
            return issuePresignedUrl(fileName, contentType);
        } catch (Exception e) {
            throw new BusinessException(ErrorType.FAILED_GET_PRE_SIGNED_URL_ERROR);
        }
    }

    public String issuePresignedUrl(String s3Key, String contentType) {
        return issuePresignedUrl(s3Key, contentType, Duration.ofMinutes(5));
    }

    public String issuePresignedUrl(String s3Key, String contentType, Duration ttl) {
        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType(contentType)
                .build();

        PresignedPutObjectRequest pre = s3Presigner.presignPutObject(
                b -> b
                        .putObjectRequest(por)
                        .signatureDuration(ttl)
        );

        return pre.url().toString();
    }

    public String getFileUrl(ImageType imageType, String fileName) {
        String path = switch (imageType) {
            case PROFILE -> profileImagePath;
            case SPOT -> tempSpotImagePath;
            case MENUBOARD -> tempMenuBoardImagePath;
        };
        return bucketUrlPrefix + path + fileName;
    }

    public void validateImageExists(String fileUrl) {
        String fileKey = getFileKey(fileUrl);
        validateImageExistsByKey(fileKey);
    }

    private String getFileKey(String fileUrl) {
        if (!fileUrl.startsWith(bucketUrlPrefix)) {
            throw new BusinessException(ErrorType.INVALID_IMAGE_PATH_ERROR);
        }

        return fileUrl.substring(bucketUrlPrefix.length());
    }

    private void validateImageExistsByKey(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorType.INVALID_IMAGE_PATH_ERROR);
        }
    }

    public void deleteFile(String fileUrl) {
        if (!fileUrl.equals(basicProfileImageUrl)) {
            try {
                String fileKey = getFileKey(fileUrl);

                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileKey)
                        .build();

                s3Client.deleteObject(deleteObjectRequest);
            } catch (Exception e) {
                log.error("Failed to delete file from S3: {}", fileUrl, e);
            }
        }
    }

    public String moveFile(String sourceUrl, String destinationKey) {
        String sourceKey = getFileKey(sourceUrl);

        try {
            CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(destinationKey)
                    .build();

            s3Client.copyObject(copyRequest);

            deleteFile(sourceUrl);

            return bucketUrlPrefix + destinationKey;
        } catch (Exception e) {
            log.error("Failed to move file from {} to {}", sourceKey, destinationKey, e);
            throw new BusinessException(ErrorType.S3_FILE_OPERATION_ERROR);
        }
    }
}
