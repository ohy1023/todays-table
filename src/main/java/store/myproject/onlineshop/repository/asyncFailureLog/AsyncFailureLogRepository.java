package store.myproject.onlineshop.repository.asyncFailureLog;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLog;

public interface AsyncFailureLogRepository extends JpaRepository<AsyncFailureLog, Long> {
}
