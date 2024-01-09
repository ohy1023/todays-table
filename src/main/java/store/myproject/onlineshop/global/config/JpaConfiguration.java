package store.myproject.onlineshop.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {
        "store.myproject.onlineshop.domain.customer.repository",
        "store.myproject.onlineshop.domain.brand.repository",
        "store.myproject.onlineshop.domain.cart.repository",
        "store.myproject.onlineshop.domain.cartitem.repository",
        "store.myproject.onlineshop.domain.delivery.repository",
        "store.myproject.onlineshop.domain.item.repository",
        "store.myproject.onlineshop.domain.membership.repository",
        "store.myproject.onlineshop.domain.order.repository",
        "store.myproject.onlineshop.domain.orderitem.repository",
        "store.myproject.onlineshop.domain.like.repository",
        "store.myproject.onlineshop.domain.recipe.repository",
        "store.myproject.onlineshop.domain.recipeitem.repository",
        "store.myproject.onlineshop.domain.review.repository",
})
@EnableTransactionManagement // 트랜잭션 관리 기능을 활성화하는 애너테이션
public class JpaConfiguration {


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            // 이름이 dataSource인 Bean을 주입 받는다.
            @Qualifier("dataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();

        // DataSource를 주입받은 dataSource로 설정한다.
        entityManagerFactory.setDataSource(dataSource);
        // JPA 엔티티 클래스가 포함된 패키지를 설정한다.
        entityManagerFactory.setPackagesToScan("store.myproject.onlineshop");
        // JPA 벤더 어뎁터를 설정한다.
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        // 영속성 유닛의 이름을 entityManager로 설정한다.
        entityManagerFactory.setPersistenceUnitName("entityManager");
        // Hibernate 속성을 설정하기 위해 별도의 Properties 객체를 사용합니다.
        entityManagerFactory.setJpaProperties(hibernateProperties());

        return entityManagerFactory;

    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        // DDL 생성 기능을 활성화
        hibernateJpaVendorAdapter.setGenerateDdl(true);
        // SQL 쿼리를 로깅하지 않도록 설정
        hibernateJpaVendorAdapter.setShowSql(false);

        hibernateJpaVendorAdapter.setDatabase(Database.MYSQL);

        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        // SQL 방언을 MySQL8Dialect 방언으로 설정
        return hibernateJpaVendorAdapter;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        // Hibernate 속성 설정
        properties.setProperty("hibernate.format_sql", "true");
        return properties;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            // 이름이 entityManager인 Bean을 주입받는다.
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        // 주입받은 entityManagerFactory의 객체를 설정한다 -> 트랜잭션 매니저가 올바른 엔티티 매니저 팩토리를 사용하여 트랜잭션을 관리할 수 있다.
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }
}