package store.myproject.onlineshop.domain.imagefile;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.item.Item;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

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
