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
    const val ALL_OPERATION = 0
    const val OPERATION_QUERY = 0b0000001
    const val OPERATION_INSERT = 0b0000010
    const val OPERATION_UPDATE = 0b0000100
    const val OPERATION_DELETE = 0b0001000

//    ContentProvider Uri的协议与权限
    const val OPERATION_SCHEME = "content://"
    const val OPERATION_AUTHORITY = "gold.sqlite.provider"

    /**
     * 以下是一些在数据操作中用到的常量标识
     */
//    插入冲突处理策略
    const val CONFLICT_STRATEGY = "conflict_strategy"
//    插入时的空值字段
    const val NULL_COLUMN_HACK = "null_column_hack"
//    是否去除重复行
    const val DISTINCT = "distinct"
//    查询Having子句
    const val HAVING = "having"
//    查询Group by子句
    const val GROUP_BY = "group_by"
//    查询Limit子句
    const val LIMIT = "limit"

    /**
     * 以下常量用于跨进程获取数据表操作者对象
     */
    const val METHOD_GET_TABLE_OPERATOR = "getTableOperator"
    const val DB_NAME = "db_name"
    const val TABLE_NAME = "table_name"

//    行Id
    const val ROW_ID = "rowId"
}