package store.myproject.onlineshop.repository.imagefile;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
