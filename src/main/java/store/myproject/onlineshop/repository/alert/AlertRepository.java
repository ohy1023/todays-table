package store.myproject.onlineshop.repository.alert;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.alert.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {
}
