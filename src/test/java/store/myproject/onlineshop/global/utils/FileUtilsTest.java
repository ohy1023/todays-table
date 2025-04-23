package store.myproject.onlineshop.global.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;

import static org.assertj.core.api.Assertions.*;

class FileUtilsTest {

    @Test
    @DisplayName("이미지 확장자(jpg, jpeg, png, gif)는 통과된다")
    void check_file_format_success() {
        assertThatCode(() -> FileUtils.checkFileFormat("image.jpg")).doesNotThrowAnyException();
        assertThatCode(() -> FileUtils.checkFileFormat("image.jpeg")).doesNotThrowAnyException();
        assertThatCode(() -> FileUtils.checkFileFormat("image.png")).doesNotThrowAnyException();
        assertThatCode(() -> FileUtils.checkFileFormat("image.gif")).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("잘못된 확장자는 AppException 예외를 발생시킨다")
    void check_file_format_fail() {
        assertThatThrownBy(() -> FileUtils.checkFileFormat("image.txt"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.WRONG_FILE_FORMAT.getMessage());

        assertThatThrownBy(() -> FileUtils.checkFileFormat("image.pdf"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.WRONG_FILE_FORMAT.getMessage());
    }

    @Test
    @DisplayName("파일명에 확장자가 없는 경우 예외가 발생한다")
    void check_file_format_fail_no_extension() {
        assertThatThrownBy(() -> FileUtils.checkFileFormat("invalidfilename"))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.WRONG_FILE_FORMAT.getMessage());
    }

    @Test
    @DisplayName("UUID로 구성된 새 파일명을 생성할 수 있다")
    void make_file_name_returns_valid_path() {
        // given
        String originalFileName = "sample.png";
        String folder = "upload/image";

        // when
        String result = FileUtils.makeFileName(originalFileName, folder);

        // then
        assertThat(result).startsWith(folder + "/");
        assertThat(result).endsWith(".png");
    }

    @Test
    @DisplayName("경로에서 파일 이름만 추출할 수 있다")
    void extract_file_name_from_path() {
        // given
        String path = "upload/image/abc1234.png";

        // when
        String result = FileUtils.extractFileName(path);

        // then
        assertThat(result).isEqualTo("abc1234.png");
    }
}
