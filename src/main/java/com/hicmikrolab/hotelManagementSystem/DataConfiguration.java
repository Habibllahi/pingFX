/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("com.hicmikrolab.hotelManagementSystem")
public class DataConfiguration {

    @Bean
    public DataSource dataSource(){
        HikariConfig hConfig = new HikariConfig();

        hConfig.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        hConfig.setJdbcUrl("jdbc:derby:AppDB;create=true");
        hConfig.setUsername("root");
        hConfig.setPassword("root");
        //hConfig.setSchema("root");
        hConfig.setAutoCommit(true);
        hConfig.setConnectionTimeout(30000L);
        hConfig.setIdleTimeout(60000L);
        hConfig.setMaxLifetime(1800000L);
        hConfig.setMinimumIdle(10);
        hConfig.setMaximumPoolSize(10);
        hConfig.setInitializationFailTimeout(1L);

        return new HikariDataSource(hConfig);
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        em.setDataSource(dataSource);

        em.setPackagesToScan("com.hicmikrolab.hotelManagementSystem");

        em.setPersistenceUnitName("punit");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        em.setJpaVendorAdapter(vendorAdapter);

        em.setJpaProperties(additionalProperties());

        return em;
    }
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {

        JpaTransactionManager transactionManager = new JpaTransactionManager();

        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){

        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor(){

        return new PersistenceAnnotationBeanPostProcessor();
    }

    public Properties additionalProperties() {

        Properties properties = new Properties();

        properties.setProperty("hibernate.hbm2ddl.auto", "update");

        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyTenSevenDialect");

        properties.setProperty("hibernate.format_sql", "true");

        return properties;
    }
}
