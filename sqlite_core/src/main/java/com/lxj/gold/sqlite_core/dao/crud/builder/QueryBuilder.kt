package com.lxj.gold.sqlite_core.dao.crud.builder

import android.text.TextUtils
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation
import com.lxj.gold.sqlite_core.dao.crud.operation.QueryOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.utils.CommonUtil
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.DISTINCT
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.GROUP_BY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.HAVING
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.LIMIT
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_QUERY
import java.lang.StringBuilder

/**
 * Created by lixinjie on 2019/9/2
 * 查询操作Builder类
 */
class QueryBuilder<T>(dbName: String, tableClass: Class<T>)
    : ABaseBuilder(dbName, CommonUtil.getTableNameFromClass(tableClass)) {
//    数据表对应实体类类型
    internal var tableClass: Class<T>? = tableClass
//    是否希望每一行唯一
    internal var distinct: Boolean = false
//    要查询的字段数组
    internal var columns: Array<String>? = null
//    选择字段条件
    internal var selection: String? = null
//    选择参数
    internal var selectionArgs: Array<String?>? = null
//    分组
    internal var groupBy: String? = null
//    分组筛选条件
    internal var having: String? = null
//    排序
    internal var orderBy: String? = null
//    限制
    internal var limit: String? = null

    /**
     * 构建查询字段条件（单字段）
     * @param column    要查询的单字段
     */
    fun select(column: String): QueryBuilder<T>{
        columns = arrayOf(column)
        return this
    }

    /**
     * 构建查询字段条件（多字段）
     * @param columnArray    查询字段数组
     */
    fun select(columnArray: Array<String>): QueryBuilder<T>{
        columns = columnArray
        return this
    }

    /**
     * 构建查询筛选条件
     * @param selectionMap   选择条件映射表
     */
    fun where(selectionMap: Map<String, String>): QueryBuilder<T>{
        if(selectionMap.isNotEmpty()){
//            初始化选择条件语句
            val whereBuilder = StringBuilder()
//            初始化选择条件value数组
            selectionArgs = arrayOfNulls(selectionMap.size)
            var index = 0
            selectionMap.forEach{
//                选择条件语句拼接
                whereBuilder.append(it.key)
                if(index == selectionMap.size - 1){
                    whereBuilder.append(" = ? ")
                }else {
                    whereBuilder.append(" = ? and ")
                }
//                选择条件赋值
                selectionArgs?.set(index, it.value)
                index ++
            }
//            选择条件语句赋值
            selection = whereBuilder.toString()
        }
        return this
    }

    /**
     * 构建Distinct关键字
     * @param isDistinct    distinct关键字赋值
     */
    fun isDistinct(isDistinct: Boolean): QueryBuilder<T> {
        this.distinct = isDistinct
        return this
    }

    /**
     * 构建分组条件
     * @param groupBy   分组条件
     */
    fun groupBy(groupBy: String): QueryBuilder<T> {
        this.groupBy = groupBy
        return this
    }

    /**
     * 构建分组筛选条件
     * @param having    分组筛选条件
     */
    fun having(having: String): QueryBuilder<T> {
        this.having = having
        return this
    }

    /**
     * 构建排序条件
     * @param orderBy    排序条件
     */
    fun orderBy(orderBy: String): QueryBuilder<T> {
        this.orderBy = orderBy
        return this
    }

    /**
     * 构建数量限制条件
     * @param limit     数量限制条件
     */
    fun limit(limit: String): QueryBuilder<T> {
        this.limit = limit
        return this
    }

    /**
     * 构建数据库查询操作
     * @return          查询操作对象
     */
    override fun build(): QueryOperation {
//        组合数据操作的Uri
        with(ArrayMap<String, String>()){
            put(DISTINCT, distinct.toString())
            if(!TextUtils.isEmpty(having))  put(HAVING, having)
            if(!TextUtils.isEmpty(groupBy)) put(GROUP_BY, groupBy)
            if(!TextUtils.isEmpty(limit)) put(LIMIT, limit)
            operationUri = UriHandler.combineOperationUriWithParms(dbName, tableName, OPERATION_QUERY, this)
        }
//        获取数据操作者对象
        val tableOperator = getTableOperator(OPERATION_QUERY)
        return QueryOperation(tableOperator, this)
    }
}