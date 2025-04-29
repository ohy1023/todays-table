package store.myproject.onlineshop.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.CommonFixture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

@ExtendWith(MockitoExtension.class)
class AwsS3ServiceTest {

    @Mock
    private AmazonS3Client amazonS3Client;

    @InjectMocks
    private AwsS3Service awsS3Service;

    @BeforeEach
    void setUp() throws Exception {
        Field field = AwsS3Service.class.getDeclaredField("bucket");
        field.setAccessible(true);
        field.set(awsS3Service, "test-bucket");
    }

    @Test
    @DisplayName("아이템 원본 이미지 업로드 성공")
    void upload_item_origin_image_success() throws IOException {
        // given
        MockMultipartFile multipartFile = CommonFixture.mockMultipartFile();

        URL mockUrl = new URL("http://s3.amazonaws.com/test-bucket/origin/item/test-image.jpg");

        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(mockUrl);

        // when
        String uploadedUrl = awsS3Service.uploadItemOriginImage(multipartFile);

        // then
        then(amazonS3Client).should(times(1)).putObject(any(PutObjectRequest.class));
        then(amazonS3Client).should(times(1)).getUrl(anyString(), anyString());

        assertThat(uploadedUrl).isEqualTo(mockUrl.toString());
    }

    @Test
    @DisplayName("이미지 업로드 실패")
    void upload_fail() {
        // given
        MockMultipartFile multipartFile = CommonFixture.mockMultipartFile();

        // S3 putObject에서 예외 발생하도록 설정
        doThrow(new AmazonS3Exception("S3 Error"))
                .when(amazonS3Client).putObject(any(PutObjectRequest.class));

        // when & then
        Assertions.assertThatThrownBy(() -> awsS3Service.uploadItemOriginImage(multipartFile))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.FILE_UPLOAD_ERROR.getMessage());

        then(amazonS3Client).should(times(1)).putObject(any(PutObjectRequest.class));

    }

    @Test
    @DisplayName("브랜드 원본 이미지 업로드 성공")
    void upload_brand_origin_image_success() throws IOException {
        // given
        MockMultipartFile multipartFile = CommonFixture.mockMultipartFile();

        URL mockUrl = new URL("http://s3.amazonaws.com/test-bucket/origin/brand/brand-logo.png");

        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(mockUrl);

        // when
        String uploadedUrl = awsS3Service.uploadBrandOriginImage(multipartFile);

        // then
        then(amazonS3Client).should(times(1)).putObject(any(PutObjectRequest.class));
        then(amazonS3Client).should(times(1)).getUrl(anyString(), anyString());

        assertThat(uploadedUrl).isEqualTo(mockUrl.toString());
    }

    @Test
    @DisplayName("레시피 원본 이미지 업로드 성공")
    void upload_recipe_origin_image_success() throws IOException {
        // given
        MockMultipartFile multipartFile = CommonFixture.mockMultipartFile();

        URL mockUrl = new URL("http://s3.amazonaws.com/test-bucket/origin/recipe/recipe-photo.jpeg");

        given(amazonS3Client.getUrl(anyString(), anyString()))
                .willReturn(mockUrl);

        // when
        String uploadedUrl = awsS3Service.uploadRecipeOriginImage(multipartFile);

        // then
        then(amazonS3Client).should(times(1)).putObject(any(PutObjectRequest.class));
        then(amazonS3Client).should(times(1)).getUrl(anyString(), anyString());

        assertThat(uploadedUrl).isEqualTo(mockUrl.toString());
    }

    @Test
    @DisplayName("아이템 이미지 삭제 성공")
    void delete_item_image_success() {
        // given
        String fileName = "item-image.jpg";

        // when
        awsS3Service.deleteItemImage(fileName);

        // then
        then(amazonS3Client).should(times(1))
                .deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("브랜드 이미지 삭제 성공")
    void delete_brand_image_success() {
        // given
        String fileName = "brand-image.jpg";

        // when
        awsS3Service.deleteBrandImage(fileName);

        // then
        then(amazonS3Client).should(times(1))
                .deleteObject(any(DeleteObjectRequest.class));
    }
}
