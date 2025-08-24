package store.myproject.onlineshop.domain.faillog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AsyncFailureLogRepository extends JpaRepository<AsyncFailureLog, Long> {
}
