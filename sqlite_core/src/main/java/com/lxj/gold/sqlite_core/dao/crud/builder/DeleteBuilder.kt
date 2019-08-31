package com.lxj.gold.sqlite_core.dao.crud.builder

import android.util.ArrayMap
import com.lxj.gold.sqlite_core.dao.crud.operation.DeleteOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_DELETE
import java.lang.StringBuilder

/**
 * Created by lixinjie on 2019/8/28
 * 删除操作Builder类
 * @param dbName    数据库名
 * @param tableName 数据表名
 */
class DeleteBuilder(dbName: String, tableName: String): ABaseBuilder(dbName, tableName){
//    删除条件语句
    var whereClause: String? = null
//    删除的筛选条件值
    var whereArgs: Array<String?>? = null

    init {
        operationUri = UriHandler.combineOperationUri(dbName, tableName, OPERATION_DELETE)
    }

    /**
     * 构建更新筛选条件（重载）
     * @param key       筛选条件字段
     * @param value     筛选条件赋值
     */
    fun where(key: String, value: String): DeleteBuilder{
        val map = ArrayMap<String, String>()
        map[key] = value
        return where(map)
    }

    /**
     * 构建删除筛选条件
     * @param whereMap  筛选条件映射表
     */
    fun where(whereMap: Map<String, String>): DeleteBuilder{
        if(whereMap.isNotEmpty()){
//            初始化选择条件语句
            val whereBuilder = StringBuilder()
//            初始化选择条件value数组
            whereArgs = arrayOfNulls(whereMap.size)
            var index = 0
            whereMap.forEach{
//                选择条件语句拼接
                whereBuilder.append(it.key)
                if(index == whereMap.size -1){
                    whereBuilder.append(" = ? ")
                }else {
                    whereBuilder.append(" = ? and ")
                }
//                选择条件赋值
                whereArgs?.set(index, it.value)
                index ++
            }
//            选择条件语句赋值
            whereClause = whereBuilder.toString()
        }
        return this
    }

    /**
     * 构建删除操作
     * @return  删除操作对象
     */
    override fun build(): DeleteOperation {
//        获取数据操作者对象
        val tableOperator = getTableOperator(OPERATION_DELETE)
        return DeleteOperation(tableOperator, this)
    }

}