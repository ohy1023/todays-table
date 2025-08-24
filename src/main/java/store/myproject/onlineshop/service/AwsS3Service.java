package store.myproject.onlineshop.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.global.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;

import static store.myproject.onlineshop.dto.common.AwsFolder.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;

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

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String originalFileName = file.getOriginalFilename();

        // 파일 형식 체크
        FileUtils.checkFileFormat(originalFileName);

        // 파일 생성
        String key = FileUtils.makeFileName(originalFileName, folder);

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata));
        }  catch (AmazonS3Exception | IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        return amazonS3Client.getUrl(bucket, key).toString();
    }

    public void deleteItemImage(String originFileName) {
        delete(ITEM.getFolderName() + "/" + originFileName, bucket);
    }

    public void deleteBrandImage(String originFileName) {
        delete(BRAND.getFolderName() + "/" + originFileName, bucket);
    }

    public void delete(String filePath, String bucket) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }


}