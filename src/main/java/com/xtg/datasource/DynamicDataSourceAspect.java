package com.xtg.datasource;


import com.xtg.datasource.annotation.TargetDataSource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

@Aspect
public class DynamicDataSourceAspect implements Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Autowired
    private DynamicDataSource dynamicDataSource;

    /**
     *  针对类上的@TargetDataSource处理，优先级低于方法上的该注解
     * @param joinPoint
     * @param classTarget
     * @throws Throwable
     */
    @Around("@within(classTarget)")
    public Object aroundByClass(ProceedingJoinPoint joinPoint, TargetDataSource classTarget) throws Throwable {
        String dataSourceName = null;
        if(classTarget != null) {
            dataSourceName = classTarget.value();
        }
        return setTargetDataSource(joinPoint, dataSourceName);
    }

    /**
     * 针对方法上的@TargetDataSource处理，优先级高于类上的该注解
     * @param joinPoint
     * @param methodTarget
     * @throws Throwable
     */
    @Around("@annotation(methodTarget)")
    public Object aroundByMethod(ProceedingJoinPoint joinPoint, TargetDataSource methodTarget) throws Throwable {
        String dataSourceName = null;
        if(methodTarget != null) {
            dataSourceName = methodTarget.value();
        }
        return setTargetDataSource(joinPoint, dataSourceName);
    }

    private Object setTargetDataSource(ProceedingJoinPoint joinPoint, String dataSourceName) throws Throwable {
        Object res;
        if(!StringUtils.isEmpty(dataSourceName)) {
            try {
                dynamicDataSource.setDataSourceKey(dataSourceName);
                res = joinPoint.proceed();
            } catch (Throwable throwable) {
                LOGGER.error("[DynamicDataSource] setTargetDataSource error", throwable);
                throw throwable;
            } finally {
                dynamicDataSource.clearDataSourceKey();
            }
        }else {
            res = joinPoint.proceed();
        }
        return res;
    }

    @Override
    public int getOrder() {
        //一定要确保在@Transactional之前，@Transactional的order为Ordered.LOWEST_PRECEDENCE
        return 0;
    }
}
