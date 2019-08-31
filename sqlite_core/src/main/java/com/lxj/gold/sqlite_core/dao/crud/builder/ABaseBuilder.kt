package com.lxj.gold.sqlite_core.dao.crud.builder

import android.net.Uri
import android.os.Bundle
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.DB_NAME
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.METHOD_GET_TABLE_OPERATOR
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.TABLE_NAME

/**
 * Created by lixinjie on 2019/8/26
 * 构建数据库操作的基本Builder接口
 * @param dbName    数据库名
 * @param tableName 数据表名
 */
abstract class ABaseBuilder(var dbName: String, var tableName: String) {
    var operationUri: Uri? = null

    /**
     * 抽象方法，构建基本数据操作
     * @return          基本数据操作对象
     */
    abstract fun build(): ABaseDbOperation

    /**
     * 获取数据库操作者类
     * @param operationType     操作类型
     * @return                  数据库操作者对象
     */
    fun getTableOperator(operationType: Int): TableOperator{
        val builderClassLoader = javaClass.classLoader
        val extras = Bundle().apply {
            this.putString(DB_NAME, dbName)
            this.putString(TABLE_NAME, tableName)
        }
        val tableOperator: TableOperator? = GoldSQLite.getContext()
            .contentResolver
            .call(UriHandler.combineOperationUri(dbName, tableName, operationType),
                METHOD_GET_TABLE_OPERATOR, null, extras)?.run {
                classLoader = builderClassLoader
                this.getParcelable(METHOD_GET_TABLE_OPERATOR)
            }
        return tableOperator?: throw GoldSQLiteCommonException("TableOperator is null.")
    }
}