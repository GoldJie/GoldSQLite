package com.lxj.gold.sqlite_core.parser.model

import com.lxj.gold.sqlite_core.dao.TableOperator

/**
 * Created by lixinjie on 2019/8/1
 * XML配置标签对应的实体：数据表
 */
class TableModel {
    companion object {
//        数据表对应的实体类（完整包名路径）
       const val ATTR_CLASS = "class"
    }

//    数据表名
    private var mTableName: String? = null
//    数据表对应实体类类型
    private var mClassName: String? = null
//    数据表操作者
    private var mTableOperator: TableOperator? = null

    fun getClassName(): String? = mClassName

    fun setClassName(className: String?) {
        this.mClassName = className
    }

    fun getTableName(): String? = mTableName


    fun setTableName(tableName: String?) {
        this.mTableName = tableName
    }

    fun getTableOperator(): TableOperator? = mTableOperator

    fun setTableOperator(tableOperator: TableOperator?) {
        this.mTableOperator = tableOperator
    }
}