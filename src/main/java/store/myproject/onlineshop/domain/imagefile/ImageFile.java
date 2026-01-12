package store.myproject.onlineshop.domain.imagefile;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.item.Item;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE image_file SET deleted_date = CURRENT_TIMESTAMP WHERE image_file_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(name = "image_file")
public class ImageFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_file_id")
    private Long id;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public static ImageFile createImage(String imageUrl) {
        return ImageFile.builder()
                .imageUrl(imageUrl)
                .build();
    }

    public void addItem(Item item) {
        this.item = item;
    }

    public void removeItem() {
        this.item = null;
    }
}
