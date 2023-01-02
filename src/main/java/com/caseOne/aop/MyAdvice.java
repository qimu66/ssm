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
                args[i] = args[i].toString().trim();
            }
        }
        //把修改后的参数放回
        Object proceed = pjp.proceed(args);
        return proceed;
    }
}
