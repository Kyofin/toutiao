package com.nowcoder.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LogAspect {
        private  static  final Logger LOGGER =  LoggerFactory.getLogger(LogAspect.class);

        @Before("execution(* com.nowcoder.controller.*Controller.*(..))")
        public void beforeMethod(JoinPoint joinPoint)
        {
                LOGGER.info("before mehod-----args:"+ joinPoint.getArgs());
                for (Object o : joinPoint.getArgs()) {
                        System.out.println("args:"+o);
                }
                LOGGER.info("before mehod-----signature:"+ joinPoint.getSignature());
                LOGGER.info("before mehod-----kind:"+ joinPoint.getKind());
        }

        @After("execution(* com.nowcoder.controller.IndexController.*(..))")
        public void afterMethod(JoinPoint joinPoint) {
                LOGGER.info("after method: ");
        }
}
