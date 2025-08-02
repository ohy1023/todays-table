package store.myproject.onlineshop.global.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@Profile("!test")
public class DataSourceConfiguration {

    public static final String MASTER_DATASOURCE = "masterDataSource";
    public static final String SLAVE_DATASOURCE = "slaveDataSource";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties("spring.datasource.master.hikari")
    public HikariDataSource masterDataSource() {
        return new HikariDataSource();
    }

    @Bean(SLAVE_DATASOURCE)
    @ConfigurationProperties("spring.datasource.slave.hikari")
    public HikariDataSource slaveDataSource() {
        return new HikariDataSource();
    }

    @Bean
    public DataSource routingDataSource(
            @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
            @Qualifier(SLAVE_DATASOURCE) DataSource slave1DataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave", slave1DataSource);

        routingDataSource.setTargetDataSources(Collections.unmodifiableMap(dataSourceMap));
        routingDataSource.setDefaultTargetDataSource(masterDataSource);

        return routingDataSource;
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}