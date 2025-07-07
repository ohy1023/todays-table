package store.myproject.onlineshop.domain.faillog;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.BaseEntity;

import java.math.BigDecimal;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsyncFailureLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "async_failure_log_id")
    private Long asyncFailureLogId;

    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "failure_status")
    @Enumerated(EnumType.STRING)
    private FailureStatus status;

}
