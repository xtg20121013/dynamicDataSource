package com.xtg.datasource.annotation;

import java.lang.annotation.*;


/**
 * 选择目标的dataSource
 * 注意：涉及事务时，请在事务开始前选择，事务过程中无法切换
 * 可以标注在类上，对类内所有的public方法有效
 * 也可以标注在方法上
 *
 * @author xtg https://github.com/xtg20121013/dynamicDataSource.git
 * @see com.xtg.datasource.DynamicDataSource
 * @see com.xtg.datasource.DynamicDataSourceRegistrar
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TargetDataSource {

    /**
     * 目标datasource的name,在配置文件中的name字段标识,找不到的话会选择默认的datasource
     * @return
     */
    String value();

}
