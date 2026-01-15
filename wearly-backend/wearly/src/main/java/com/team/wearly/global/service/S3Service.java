package com.team.wearly.global.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import com.team.wearly.global.util.PresignedUrlVo;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObject;

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

    // 상품 전용 업로드 URL 발급 (폴더명 자유 설정 가능)
    public String[] createProductPresignedUrl(String folderName, String extension) {
        // 1. 키 생성
        String fileName = UUID.randomUUID().toString() + "." + extension;
        String key = folderName + "/" + fileName;

        // 2. 파일 타입 설정
        String contentType = "image/" + extension;

        // 3. AWS 요청 생성
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket) // bucket 변수 사용
                .key(key)
                .contentType(contentType)
                .build();

        // 4. 서명(권한) 발급 요청 생성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        // 5. URL 발급 (Import 오류 방지를 위해 체이닝 방식 사용)
        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        // 6. 결과 반환 (팀원 코드와 동일하게 String 배열 사용)
        return new String[]{url, key};
    }
}