package com.qimu.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

public class JdbcConfig {
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.password}")
    private String password;
    @Value("${jdbc.username}")
    private String username;

    @Bean
    public DataSource dataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setPassword(password);
        ds.setUsername(username);
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        return ds;
    }

    /**
     * 事务
     * @param dataSource
     * @return
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        DataSourceTransactionManager dst = new DataSourceTransactionManager();
        dst.setDataSource(dataSource);
        return dst;
    }
}
