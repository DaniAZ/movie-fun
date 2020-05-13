package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class Config {
    @Autowired
    DatabaseServiceCredentials databaseServiceCredentials;
    @Bean
    public DataSource albumsDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("albums-mysql"));
        return dataSource;
    }
    @Bean
    public DataSource moviesDataSource(DatabaseServiceCredentials serviceCredentials) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(serviceCredentials.jdbcUrl("movies-mysql"));
        return dataSource;
    }
    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter(){
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setShowSql(true);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        vendorAdapter.setGenerateDdl(true);
        return vendorAdapter;
    }
    @Bean(name = "moviesEntity")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryMovie() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(albumsDataSource(databaseServiceCredentials));
        em.setPackagesToScan(new String[] { "org.superbiz.moviefun.movies" });
       em.setJpaVendorAdapter(getHibernateJpaVendorAdapter());
       em.setPersistenceUnitName("moviesEntity");
       return em;
    }
    @Bean("albumsEntity")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryAlbum() {
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(albumsDataSource(databaseServiceCredentials));
        em.setPackagesToScan(new String[] { "org.superbiz.moviefun.albums" });
        em.setJpaVendorAdapter(getHibernateJpaVendorAdapter());
        em.setPersistenceUnitName("albumsEntity");
        return em;
    }
    @Bean(name = "moviesTransactionManager")
    public PlatformTransactionManager moviesTransactionManager(
            @Qualifier("moviesEntity") EntityManagerFactory moviesEntityManagerFactory) {
        return new JpaTransactionManager(moviesEntityManagerFactory);
    }
    @Bean(name = "albumsTransactionManager")
    public PlatformTransactionManager albumsTransactionManager(
            @Qualifier("albumsEntity") EntityManagerFactory albumsEntityManagerFactory) {
        return new JpaTransactionManager(albumsEntityManagerFactory);
    }
}
