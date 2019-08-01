package com.lxj.gold.sqlite_core.dao

/**
 * Created by lixinjie on 2019/8/1
 * 数据表DAO操作类
 * 用于限制对单表的并发操作
 * @param mTableName    数据表名
 */
class TableOperator(var mTableName: String) {
}