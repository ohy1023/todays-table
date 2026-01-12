package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.global.utils.FileUtils;

import java.io.IOException;

import static store.myproject.onlineshop.dto.common.AwsFolder.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadItemOriginImage(MultipartFile file) {
        return upload(file, bucket, ITEM.getFolderName());
    }

    public String uploadBrandOriginImage(MultipartFile file) {
        return upload(file, bucket, BRAND.getFolderName());
    }

    public String uploadRecipeOriginImage(MultipartFile file) {
        return upload(file, bucket, RECIPE.getFolderName());
    }

    public String upload(MultipartFile file, String bucket, String folder) {
        String originalFileName = file.getOriginalFilename();

        // 파일 형식 체크
        FileUtils.checkFileFormat(originalFileName);

        // 파일명 생성 (UUID 기반)
        String key = FileUtils.makeFileName(originalFileName, folder);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // S3 업로드 실행
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 반환
            return s3Client.utilities().getUrl(GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build()).toString();

        } catch (S3Exception | IOException e) {
            log.error("S3 업로드 중 에러 발생: {}", e.getMessage());
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    public void deleteItemImage(String originFileName) {
        delete(ITEM.getFolderName() + "/" + originFileName, bucket);
    }

    public void deleteBrandImage(String originFileName) {
        delete(BRAND.getFolderName() + "/" + originFileName, bucket);
    }

    public void delete(String filePath, String bucket) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            log.error("S3 파일 삭제 중 에러 발생: {}", e.getMessage());
            // 삭제 실패 시 비즈니스 로직에 따라 예외를 던질지 결정 (보통 로그만 남김)
        }
    }
}