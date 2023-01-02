package com.qimu.config;

import org.springframework.context.annotation.*;
@Configuration
//@EnableAspectJAutoProxy
@ComponentScan("com.qimu")
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class,MybatisConfig.class})
public class UserConfig {
}
