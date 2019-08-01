package com.lxj.gold.sqlite_core.annotation

/**
 * Created by lixinjie on 2019/8/1
 * 数据表字段注解，建立JavaBean字段与数据表字段映射
 * @param name      字段名称
 * @param type      字段类型
 * @param length    字段长度
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val name: String = "", val type: String = "", val length: Int = 0)