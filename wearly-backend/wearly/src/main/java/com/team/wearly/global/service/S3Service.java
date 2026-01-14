package com.team.wearly.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    /**
     * 프로필 이미지 업로드를 위한 Presigned URL 생성
     *
     * @param userId      사용자 ID
     * @param contentType 이미지 Content-Type (예: image/jpeg, image/png)
     */
    public String[] createPresignedUrl(Long userId, String contentType, String userType) {
        String extension = contentType.split("/")[1];  // image/png -> ["image","png"] 배열로 변경
        String key = String.format("profiles/%s/%d/%s.%s", userType, userId, UUID.randomUUID(), extension);

        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        var preSignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(preSignRequest).url().toString();
        return new String[]{presignedUrl, key};  //key -> 업로드될 이미지의 경로
    }
}