package store.myproject.onlineshop.fixture;

import org.springframework.mock.web.MockMultipartFile;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CommonFixture {

    public static MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile(
                "multipartFile",
                "image.png",
                "image/png",
                "<<image data>>".getBytes(StandardCharsets.UTF_8)
        );
    }

    public static List<MockMultipartFile> mockMultipartFileList() {
        MockMultipartFile file1 = new MockMultipartFile(
                "multipartFileList",
                "test-image1.jpg",
                "image/jpeg",
                "fake-image-content-1".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile file2 = new MockMultipartFile(
                "multipartFileList",
                "test-image2.jpg",
                "image/jpeg",
                "fake-image-content-2".getBytes(StandardCharsets.UTF_8)
        );

        return List.of(file1, file2);
    }
}
