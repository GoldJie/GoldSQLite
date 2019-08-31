package com.lxj.gold.sqlite_core.dao.crud.builder

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase.CONFLICT_NONE
import android.util.ArrayMap
import com.lxj.gold.sqlite_core.dao.crud.operation.UpdateOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.db.TableHelper
import com.lxj.gold.sqlite_core.utils.CommonUtil
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.CONFLICT_STRATEGY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_UPDATE
import java.lang.StringBuilder

/**
 * Created by lixinjie on 2019/8/31
 * 更新操作Builder类
 */
class UpdateBuilder(dbName: String, tableName: String): ABaseBuilder(dbName, tableName) {
//    需要更新的values键值对映射
    var valuesMap: ArrayMap<String, Any>? = null
//    需要更新的values键值对
    var contentValues: ContentValues? = null
//    更新条件语句
    var whereClause: String? = null
//    更新筛选条件值
    var whereArgs: Array<String?>? = null
//    更新冲突处理策略
    var conflictAlgorithm = CONFLICT_NONE

    /**
     * 构建要更新字段的键值对
     * @param columnName    数据表字段名
     * @param value         更新数值
     */
    fun update(columnName: String, value: Any): UpdateBuilder{
        valuesMap = valuesMap?: ArrayMap()
        valuesMap?.put(columnName, value)
        return this
    }

    /**
     * 通过数据表实体类构建更新字段
     * @param tableEntity   数据表实体类
     */
    fun updateByEntity(tableEntity: Any): UpdateBuilder{
        valuesMap = TableHelper.transformEntityToValuesMap(tableEntity)
        return this
    }

    /**
     * 构建更新筛选条件（重载）
     * @param key       筛选条件字段
     * @param value     筛选条件赋值
     */
    fun where(key: String, value: String): UpdateBuilder{
        val map = ArrayMap<String, String>()
        map[key] = value
        return this
    }

    /**
     * 构建更新筛选条件
     * @param whereMap  筛选条件映射表
     */
    fun where(whereMap: Map<String, String>): UpdateBuilder{
        if(whereMap.isNotEmpty()){
//            初始化选择条件语句
            val whereBuilder = StringBuilder()
//            初始化选择条件value数组
            whereArgs = arrayOfNulls(whereMap.size)
            var index = 0
            whereMap.forEach{
//                选择条件语句拼接
                whereBuilder.append(it.key)
                if(index == whereMap.size - 1){
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
     * 构建更新时的冲突处理策略
     * @param conflictAlgorithm     更新时的冲突处理策略
     */
    fun setConflictStrategy(conflictAlgorithm: Int): UpdateBuilder{
        this.conflictAlgorithm = conflictAlgorithm
        return this
    }

    /**
     * 构建更新操作
     * @return          更新操作对象
     */
    override fun build(): UpdateOperation {
        valuesMap?.let {
//            通过映射表构建ContentValue
            contentValues = CommonUtil.combineContentValues(it)
//            组合数据操作的Uri
            val parmMap = ArrayMap<String, String>()
            parmMap.put(CONFLICT_STRATEGY, conflictAlgorithm.toString())
            operationUri = UriHandler.combineOperationUriWithParms(dbName, tableName, OPERATION_UPDATE, parmMap)
        }
//        获取数据操作者对象
        val tableOperator = getTableOperator(OPERATION_UPDATE)
        return UpdateOperation(tableOperator, this)
    }
}