## AOP

[TOC]

## 数据返回结果统一处理

![image-20230103211759955](https://img2023.cnblogs.com/blog/2923613/202301/2923613-20230103211812714-20207968.png)

![image-20230103211820824](https://img2023.cnblogs.com/blog/2923613/202301/2923613-20230103211833193-255092865.png)

![image-20230103211858060](https://img2023.cnblogs.com/blog/2923613/202301/2923613-20230103211910445-250889069.png)

```java
/**
 * 返回工具类
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse(errorCode.getCode(), null, message);
    }
}
```

```java
import java.io.Serializable;
import lombok.Data;

/**
 * 通用返回类
 *
 * @param <T>
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
```

## 异常处理器

![image-20230103212912152](https://img2023.cnblogs.com/blog/2923613/202301/2923613-20230103212924536-1282831147.png)

![image-20230103212944438](https://img2023.cnblogs.com/blog/2923613/202301/2923613-20230103212956796-130362154.png)

```java
/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("runtimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }
}
```

```java
/**
 * 自定义异常类
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
```

```java
/**
 * 错误码
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
```



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

