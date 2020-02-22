package com.toomuch2learn.springboot2.crud.catalogue.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {

    private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Pointcut("within(com.toomuch2learn.springboot2.crud.catalogue.controller..*)")
    public void controllerPointcut() { }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        log.error("===> Exception occurred in {} with cause = {}",
            joinPoint.getSignature().getName(),
            e.getMessage());
    }

    @Around("controllerPointcut()")
    public Object logAroundControllerEvents(ProceedingJoinPoint joinPoint) throws Throwable {
        LocalDateTime begin = LocalDateTime.now();
        if(log.isDebugEnabled()) {
            log.debug("===> Begin: {} @ {}",
                joinPoint.getSignature().getName(),
                begin);
        }
        try {
            Object response = joinPoint.proceed();
            LocalDateTime end = LocalDateTime.now();
            if (log.isDebugEnabled()) {
                log.debug("===> End: {} completed @ {} within {} secs",
                    joinPoint.getSignature().getName(),
                    end,
                    Duration.between(begin, end).getSeconds());
            }
            return response;
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
    }
}
