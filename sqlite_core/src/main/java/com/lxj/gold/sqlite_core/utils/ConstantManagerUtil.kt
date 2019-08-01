package com.lxj.gold.sqlite_core.utils

/**
 * Created by lixinjie on 2019/8/1
 * 全局常量管理工具类
 */
object ConstantManagerUtil {
//    数据库全局配置XML文件名
    const val CONFIG_XML_FILE_NAME = "GoldSQLite"

    /**
     * SQLite数据库基础数据类型常量
     */
    const val COLUMN_TYPE_STRING = "text"
    const val COLUMN_TYPE_INT = "integer"
    const val COLUMN_TYPE_LONG = "bigint"
    const val COLUMN_TYPE_FLOAT = "float"
    const val COLUMN_TYPE_DOUBLE = "double"
    const val COLUMN_TYPE_BOOLEAN = "boolean"
    const val COLUMN_TYPE_BLOB = "blob"

    /**
     * 数据表的操作类型，用于监听数据表时鉴别数据表操作
     */
    const val OPERATION_QUERY = "operation_query"
    const val OPERATION_INSERT = "operation_insert"
    const val OPERATION_UPDATE = "operation_update"
    const val OPERATION_DELETE = "operation_delete"

}