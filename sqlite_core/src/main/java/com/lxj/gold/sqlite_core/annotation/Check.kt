package com.lxj.gold.sqlite_core.annotation

/**
 * Created by lixinjie on 2019/8/1
 * 标识SQL语句中的Check限制关键字
 * @param value Check语句内容
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Check(val value: String)