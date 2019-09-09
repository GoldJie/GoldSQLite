package com.lxj.gold.sqlite_core.dao.crud.builder

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_NONE
import android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.dao.crud.operation.InsertOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.db.TableHelper
import com.lxj.gold.sqlite_core.utils.CommonUtil
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.CONFLICT_STRATEGY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.NULL_COLUMN_HACK
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_INSERT

/**
 * Created by lixinjie on 2019/9/2
 * 插入操作Builder类
 * @param dbName         数据库名称
 * @param tableName      数据表名
 */
class InsertBuilder(dbName: String, tableName: String): ABaseBuilder(dbName, tableName){
//    标识是否是多条插入
    internal var isBulkInsert: Boolean = false
//    插入空行的列名
    internal var nullColumnHack: String? = null
//    需要更新的values键值对映射表
    internal var valuesMap: Map<String, Any>? = null
//    插入数据键值对列表
    internal var contentValuesList: MutableList<ContentValues>? = null
//    插入数据的键值对
    internal var contentValues: ContentValues? = null
//    插入冲突解决策略（默认覆盖）
    internal var conflictAlgorithm = CONFLICT_REPLACE

    /**
     * 构建空行列名
     * @param nullColumnHack    数值为空的列
     */
    fun setNullColumnHack(nullColumnHack: String): InsertBuilder{
        this.nullColumnHack = nullColumnHack
        return this
    }

    /**
     * 构建插入的数据
     * @param tableEntity       数据表对应实体
     */
    fun insert(tableEntity: Any): InsertBuilder{
        valuesMap = TableHelper.transformEntityToValuesMap(tableEntity)
        contentValues = valuesMap?.let { CommonUtil.combineContentValues(it) }
        return this
    }

    /**
     * 构建多行插入数据
     * @param tableEntityList   数据表对应实体列表
     */
    fun bulkInsert(tableEntityList: List<Any>): InsertBuilder{
        contentValuesList = contentValuesList?: ArrayList()
//        遍历实体列表组合ContentValue列表
        tableEntityList.forEach {
            val contentValue = TableHelper.transformEntityToValuesMap(it)?.let { it1 -> CommonUtil.combineContentValues(it1) }
            contentValue?.run {
                contentValuesList?.add(this)
            }
        }
        isBulkInsert = true
        return this
    }

    /**
     * 构建插入时的冲突处理策略
     * @param conflictAlgorithm     插入时的冲突处理策略
     */
    fun setConflictStrategy(conflictAlgorithm: Int): InsertBuilder{
        this.conflictAlgorithm = conflictAlgorithm
        return this
    }

    /**
     * 构建插入操作
     * @return          插入操作对象
     */
    override fun build(): InsertOperation {
//        组合数据操作的Uri
        with(ArrayMap<String, String>()){
            put(CONFLICT_STRATEGY, conflictAlgorithm.toString())
            put(NULL_COLUMN_HACK, nullColumnHack)
            operationUri = UriHandler.combineOperationUriWithParms(dbName, tableName, OPERATION_INSERT, this)
        }
//        获取数据操作者对象
        val tableOperator = getTableOperator(OPERATION_INSERT)
        return InsertOperation(tableOperator, this)
    }
}