package store.myproject.onlineshop.domain.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
