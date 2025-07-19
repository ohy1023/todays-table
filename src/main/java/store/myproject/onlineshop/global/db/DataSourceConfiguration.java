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
    public static final String SLAVE1_DATASOURCE = "slave1DataSource";
    public static final String SLAVE2_DATASOURCE = "slave2DataSource";

    @Bean(MASTER_DATASOURCE)
    @ConfigurationProperties("spring.datasource.master.hikari")
    public HikariDataSource masterDataSource() {
        return new HikariDataSource();
    }

    @Bean(SLAVE1_DATASOURCE)
    @ConfigurationProperties("spring.datasource.slave1.hikari")
    public HikariDataSource slaveDataSource() {
        return new HikariDataSource();
    }

    @Bean(SLAVE2_DATASOURCE)
    @ConfigurationProperties("spring.datasource.slave2.hikari")
    public HikariDataSource slave2DataSource() {return new HikariDataSource();}

    @Bean
    public DataSource routingDataSource(
            @Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
            @Qualifier(SLAVE1_DATASOURCE) DataSource slave1DataSource,
            @Qualifier(SLAVE2_DATASOURCE) DataSource slave2DataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave1", slave1DataSource);
        dataSourceMap.put("slave2", slave2DataSource);

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