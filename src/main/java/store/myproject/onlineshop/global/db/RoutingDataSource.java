package store.myproject.onlineshop.global.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private static final String MASTER = "master";
    private static final List<String> SLAVES = List.of("slave1", "slave2");

    private final AtomicInteger slaveIndex = new AtomicInteger(0);

    @Override
    protected Object determineCurrentLookupKey() {
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly) {

            int index = Math.abs(slaveIndex.getAndIncrement() % SLAVES.size());
            String selectedSlave = SLAVES.get(index);

            log.info("읽기 트랜잭션 -> {} DataSource 선택", selectedSlave);
            return selectedSlave;
        } else {
            log.info("쓰기 트랜잭션 -> master DataSource 선택");
            return MASTER;
        }
    }
}
