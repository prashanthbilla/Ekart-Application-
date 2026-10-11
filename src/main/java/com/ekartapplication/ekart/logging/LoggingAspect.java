package com.ekartapplication.ekart.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
            " || within(@org.springframework.stereotype.Service *)" +
            " || within(@org.springframework.web.bind.annotation.RestController *)")
    public void springBeanPointcut() {
    }

    @Pointcut("within(com.ekartapplication.ekart..*)")
    public void applicationPackagePointcut() {
    }

    @Around("applicationPackagePointcut() && springBeanPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            logger.info("Entering {}.{}", className, methodName);

            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Exiting {}.{} | Execution Time: {} ms",
                    className, methodName, executionTime);

            return result;

        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;

            logger.error("Exception in {}.{}() | Time: {} ms | Error: {}",
                    className, methodName, executionTime, ex.getMessage());

            throw ex;
        }
    }

//    public void enter(){
//
//    }
//
//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_YELLOW = "\u001B[33m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//    public static final String ANSI_PURPLE = "\u001B[35m";
//    public static final String ANSI_CYAN = "\u001B[36m";
//    public static final String ANSI_WHITE = "\u001B[37m";
//
//    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
//    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
//    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
//    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
//    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
//    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
//    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
//    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
//
//    Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
//
//
//    @Pointcut("within(@org.springframework.stereotype.Repository *)" +
//            " || within(@org.springframework.stereotype.Service *)" +
//            " || within(@org.springframework.web.bind.annotation.RestController *)")
//    public void springBeanPointcut() {
//        // Method is empty as this is just a Pointcut, the implementations are in the advices.
//    }
//
//    @Pointcut("within(com.ekartapplication.ekart..*)" )
//    public void applicationPackagePointcut() {
//        // Method is empty as this is just a Pointcut, the implementations are in the advices.
//    }
//
//    @Around("applicationPackagePointcut() && springBeanPointcut()")
//    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        Object[] args = joinPoint.getArgs();
//        String methodName = joinPoint.getSignature().getName();
//        //logger.debug(">> {}() - {}", methodName, Arrays.toString(args));
//        Object result = joinPoint.proceed();
//        //logger.debug("<< {}() - {}", methodName, result);
//        final String className = joinPoint.getTarget().getClass().getSimpleName();
//        logger.info(ANSI_GREEN + " ENTERING "+ANSI_RESET);
//        logger.info("TIME :: "+LocalDateTime.now() +ANSI_PURPLE + "\nClass :: " + ANSI_CYAN+className
//                +"\n"+ANSI_BLUE+ "Method :: " +ANSI_CYAN+ methodName +"()"+ANSI_YELLOW+"\nResults :: " + result+"\n"+ANSI_RESET);
//        logger.info(ANSI_GREEN + " EXITING "+ANSI_RESET);
//        return result;
//
//    }
//
//
//    // @Pointcut("within(com.ekartapplication.ekart..*)" +
//    //            "within(com.ekartapplication.ekart.controllers..*)")
}