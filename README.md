## AOP

### 注解方式实现aop

1. 导入aop相关依赖
2. 定义接口和实现类
3. 定义通知类
4. 定义切入点 `@Pointcut("execution(")") `,无参无返回值的方法加上这个注解
5. 用`@Component`定义通知类受spring容器管理，用`@Aspect`定义当前类为切面类
6. 在配置类上加上<span style="color:red">`@EnableAspectJAutoProxy`</span>注解开启对aop注解驱动的支持

