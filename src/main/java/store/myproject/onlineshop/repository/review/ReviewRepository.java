package store.myproject.onlineshop.repository.review;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
