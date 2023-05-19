package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Where(clause = "deleted_date is null")
@SQLDelete(sql = "UPDATE review SET deleted_date = CURRENT_TIME WHERE review_id = ?")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewPhoto> reviewPhotoList = new ArrayList<>();

    private String content;

    public void update(String content) {
        this.content = content;
    }

    @Builder
    public Review(Long reviewId, Customer customer, Item item, List<ReviewPhoto> reviewPhotoList, String content) {
        this.reviewId = reviewId;
        this.customer = customer;
        this.item = item;
        this.reviewPhotoList = reviewPhotoList;
        this.content = content;
    }
}