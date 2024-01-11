package store.myproject.onlineshop.domain.imagefile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
}
