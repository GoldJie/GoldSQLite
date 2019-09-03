package com.lxj.gold.sqlite_core.annotation

/**
 * Created by lixinjie on 2019/8/1
 * 数据表注解，建立JavaBean与数据库表的映射关系
 * @param tableName  数据表名
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TableName(val tableName: String = "")