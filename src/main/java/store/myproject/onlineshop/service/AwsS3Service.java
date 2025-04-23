package store.myproject.onlineshop.service;

import com.amazonaws.services.s3.AmazonS3Client;
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

import static store.myproject.onlineshop.domain.AwsConstants.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadItemOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_ITEM_FOLDER);
    }

    public String uploadBrandOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_BRAND_FOLDER);
    }
    public String uploadRecipeOriginImage(MultipartFile file) {
        return upload(file, bucket, ORIGIN_RECIPE_FOLDER);
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
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        String storedFileUrl = amazonS3Client.getUrl(bucket, key).toString();

        return storedFileUrl;
    }

    public void deleteProductImage(String originFileName) {
        delete(ORIGIN_ITEM_FOLDER + "/" + originFileName, bucket);
    }

    public void deleteBrandImage(String originFileName) {
        delete(ORIGIN_BRAND_FOLDER + "/" + originFileName, bucket);
    }

    public void delete(String filePath, String bucket) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, filePath));
    }


}