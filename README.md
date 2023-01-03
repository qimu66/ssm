## AOP

[TOC]

### 注解方式实现aop

1. 导入aop相关依赖  

   ```properties
   <dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-webmvc</artifactId>
   <version>5.2.8.RELEASE</version>
   </dependency>
   <dependency>
   <groupId>org.springframework</groupId>
   <artifactId>spring-aspects</artifactId>
   <version>5.3.1</version>
   </dependency>
   ```

2. 定义接口和实现类

3. 定义通知类

4. 定义切入点 `@Pointcut("execution(")") `,无参无返回值的方法加上这个注解

5. 用`@Component`定义通知类受spring容器管理，用`@Aspect`定义当前类为切面类

6. 在配置类上加上<span style="color:red">`@EnableAspectJAutoProxy`</span>注解开启对aop注解驱动的支持

### Aop案例

#### 配置类

```java
package com.caseOne.config;

import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan("com.caseOne")
public class UrlConfig {
}
```

#### 接口和业务层

##### dao

```java
package com.caseOne.dao.impl;

import com.caseOne.dao.UrlDAO;
import org.springframework.stereotype.Repository;

@Repository
public class UrlDaoImpl implements UrlDAO {
    @Override
    public boolean isPassword(String url, String password) {
        return password.equals("root");
    }
}
```

```java
package com.caseOne.dao;

public interface UrlDAO {
    boolean isPassword(String url,String password);
}

```

##### service

```java
package com.caseOne.service.impl;

import com.caseOne.dao.UrlDAO;
import com.caseOne.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlServiceImpl implements UrlService {
    @Autowired
    private UrlDAO urlDAO;

    @Override
    public boolean isRoot(String url, String password) {
        System.out.println(password.length());
        return urlDAO.isPassword(url, password);
    }
}
```

```java
package com.caseOne.service;

public interface UrlService {
    boolean isRoot(String url,String password);
}

```

#### 通知类（切面类）

```java
package com.caseOne.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAdvice {
    @Pointcut("execution(* com.caseOne.service.*Service.*(*,*))")
    public void pt() {
    }

    @Around("pt()")
    public Object trimStr(ProceedingJoinPoint pjp) throws Throwable {
        //获取切点的参数值
        Object[] args = pjp.getArgs();
        for (int i = 0; i < args.length; i++) {
            //判断参数是不是字符串
            if (args[i].getClass().equals(String.class)) {
                //去除空格
                args[i] = args[i].toString().trim();
            }
        }
        //把修改后的参数放回
        Object proceed = pjp.proceed(args);
        return proceed;
    }
}
```

#### 测试类

```java
package com.caseOne;
import com.caseOne.config.UrlConfig;
import com.caseOne.service.UrlService;
import com.qimu.config.UserConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestUrl {
    @Autowired
    private UrlService urlService;

    @Test
    public void testUrl() {
        AnnotationConfigApplicationContext ctx =
                new AnnotationConfigApplicationContext(UrlConfig.class);
        UrlService bean = ctx.getBean(UrlService.class);
        boolean root1 = bean.isRoot("https:www.qimu.com", "root ");
        System.out.println(root1);
//        boolean root = urlService.isRoot("https:www.qimu.com", "root");
//        System.out.println(root);
    }
}
```

### AOP整合MyBatis

```java
package com.qimu.config;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class MybatisConfig {
    @Bean
    public SqlSessionFactoryBean sessionFactoryBean(DataSource dataSource) {
        SqlSessionFactoryBean sfb = new SqlSessionFactoryBean();
        sfb.setDataSource(dataSource);
        sfb.setTypeAliasesPackage("com.qimu");
        return sfb;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer msc = new MapperScannerConfigurer();
        msc.setBasePackage("com.qimu");
        return msc;
    }
}
```

##### 数据源配置

```java
package com.qimu.config;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

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
}
```

##### 测试类

```java
package com.qimu.config;

import org.springframework.context.annotation.*;
@Configuration
@ComponentScan("com.qimu")
@PropertySource("classpath:jdbc.properties")
@Import({JdbcConfig.class,MybatisConfig.class})
public class UserConfig {
}
```

### 事务管理器

1. 在业务层接口上加上`@Transactional`注解

2. 配置类

   ```java
   @Bean
   public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
       DataSourceTransactionManager dst = new DataSourceTransactionManager();
       dst.setDataSource(dataSource);
       return dst;
   }
   ```

   

3. 注解 在配置类上加上`@EnableTransactionManagement`注解

## SpringMvc

配置类

```java
package com.springmvc.config;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

public class WebConfig extends AbstractDispatcherServletInitializer {
    //加载springmvc容器配置
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext acwc = new AnnotationConfigWebApplicationContext();
        acwc.register(SpringMvcConfig.class);
        return acwc;
    }
     //设置那些请求归属springmvc
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    //加载spring容器配置
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return null;
    }
}

```

```java
package com.springmvc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.springmvc")
public class SpringMvcConfig {
}
```

