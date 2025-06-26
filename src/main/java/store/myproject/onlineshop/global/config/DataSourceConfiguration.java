package store.myproject.onlineshop.global.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import store.myproject.onlineshop.global.db.RoutingDataSource;

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
            @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("master", masterDataSource);
        dataSourceMap.put("slave", slaveDataSource);

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



//package store.myproject.onlineshop.global.config;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Profile;
//import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
//import store.myproject.onlineshop.global.db.RoutingDataSource;
//
//import javax.sql.DataSource;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//@Profile("!test")
//public class DataSourceConfiguration {
//
//    public static final String MASTER_DATASOURCE = "masterDataSource";
//    public static final String SLAVE_DATASOURCE = "slaveDataSource";
//
//    @Value("${spring.datasource.master.hikari.driver-class-name}")
//    private String masterDriverClassName;
//
//    @Value("${spring.datasource.master.hikari.jdbc-url}")
//    private String masterJdbcUrl;
//
//    @Value("${spring.datasource.master.hikari.username}")
//    private String masterUsername;
//
//    @Value("${spring.datasource.master.hikari.password}")
//    private String masterPassword;
//
//    @Value("${spring.datasource.slave.hikari.driver-class-name}")
//    private String slaveDriverClassName;
//
//    @Value("${spring.datasource.slave.hikari.jdbc-url}")
//    private String slaveJdbcUrl;
//
//    @Value("${spring.datasource.slave.hikari.username}")
//    private String slaveUsername;
//
//    @Value("${spring.datasource.slave.hikari.password}")
//    private String slavePassword;
//
//    @Bean(MASTER_DATASOURCE)
//    public DataSource masterDataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName(masterDriverClassName)
//                .url(masterJdbcUrl)
//                .username(masterUsername)
//                .password(masterPassword)
//                .build();
//    }
//
//    @Bean(SLAVE_DATASOURCE)
//    public DataSource slaveDataSource() {
//        return DataSourceBuilder.create()
//                .driverClassName(slaveDriverClassName)
//                .url(slaveJdbcUrl)
//                .username(slaveUsername)
//                .password(slavePassword)
//                .build();
//    }
//
//    @Bean
//    public DataSource routingDataSource(@Qualifier(MASTER_DATASOURCE) DataSource masterDataSource,
//                                        @Qualifier(SLAVE_DATASOURCE) DataSource slaveDataSource) {
//
//        RoutingDataSource routingDataSource = new RoutingDataSource();
//
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("master", masterDataSource);
//        dataSourceMap.put("slave", slaveDataSource);
//
//        Map<Object, Object> immutableDataSourceMap = Collections.unmodifiableMap(dataSourceMap);
//
//        routingDataSource.setTargetDataSources(immutableDataSourceMap);
//        routingDataSource.setDefaultTargetDataSource(masterDataSource);
//
//        return routingDataSource;
//    }
//
//    @Primary
//    @Bean
//    public DataSource dataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
//        return new LazyConnectionDataSourceProxy(routingDataSource);
//    }
//
//}