package com.lxj.gold.sqlite_core.dao.crud.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CONFLICT_NONE
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import com.lxj.gold.sqlite_core.db.DbManager
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.utils.CommonUtil
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.CONFLICT_STRATEGY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.DB_NAME
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.GROUP_BY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.HAVING
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.LIMIT
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.METHOD_GET_TABLE_OPERATOR
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.NULL_COLUMN_HACK
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_AUTHORITY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_SCHEME
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.ROW_ID
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.TABLE_NAME

/**
 * Created by lixinjie on 2019/8/28
 * ContentProvider组件，增删改查实际操作执行者，同时为了方便跨进程使用
 */
class SQLiteProvider: ContentProvider(){
//    匹配Uri返回码
    private val OPERATION_CODE = 0
//    Uri匹配器
    private var mUriMatcher: UriMatcher? = null

    override fun onCreate(): Boolean {
        mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        mUriMatcher?.addURI(OPERATION_AUTHORITY, null, OPERATION_CODE)
//        初始化数据库配置
        context?.let {  DbManager.initDatabase(it) }
        return true
    }

    /**
     * 插入
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        try {
            val dbName = UriHandler.getDbNameFromUri(uri)
            val tableName = UriHandler.getTableNameFromUri(uri)
//            获取Uri携带的额外参数
            val nullColumnHank = UriHandler.getParmFromUri(uri, NULL_COLUMN_HACK)
            val conflictStrategy = CommonUtil
                .transformStringToInt(UriHandler.getParmFromUri(uri, CONFLICT_STRATEGY))
                .apply {
                    if(this == -1) CONFLICT_NONE
                }

            if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                val db = DbManager.getSQLiteDb(dbName)
                val rowId = db.insertWithOnConflict(tableName, nullColumnHank, values, conflictStrategy)
//                通知数据表监听器
                context?.contentResolver?.notifyChange(uri, null)
                return Uri.parse("$OPERATION_SCHEME$OPERATION_AUTHORITY?$ROW_ID=$rowId")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("insert error: " + e.message)
        }
        return null
    }

    /**
     * 多行插入
     */
    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        var rowCount = 0
        try {
            val dbName = UriHandler.getDbNameFromUri(uri)
            val tableName = UriHandler.getTableNameFromUri(uri)
//            获取Uri携带的额外参数
            val nullColumnHank = UriHandler.getParmFromUri(uri, NULL_COLUMN_HACK)
            val conflictStrategy = CommonUtil
                .transformStringToInt(UriHandler.getParmFromUri(uri, CONFLICT_STRATEGY))
                .apply {
                    if(this == -1) CONFLICT_NONE
                }

            if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                val db = DbManager.getSQLiteDb(dbName)
                if(values.isNotEmpty()){
    //                开启事务
                    db.beginTransaction()
                    values.forEach {
                        val rowId = db.insertWithOnConflict(tableName, nullColumnHank, it, conflictStrategy)
                        if(rowId != -1L){
                            rowCount ++
                        }
                    }
//                    事务结束
                    db.setTransactionSuccessful()
                    db.endTransaction()
//                    通知数据表监听器
                    context?.contentResolver?.notifyChange(uri, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("bulkInsert error: " + e.message)
        }
        return rowCount
    }

    /**
     * 查询
     */
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        try {
            val dbName = UriHandler.getDbNameFromUri(uri)
            val tableName = UriHandler.getTableNameFromUri(uri)
//            获取Uri携带的额外参数
            val distinct = UriHandler.getDistinctFromUri(uri)
            val groupBy = UriHandler.getParmFromUri(uri, GROUP_BY)
            val having = UriHandler.getParmFromUri(uri, HAVING)
            val limit = UriHandler.getParmFromUri(uri, LIMIT)

            if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                val db = DbManager.getSQLiteDb(dbName)
                val cursor = db.query(distinct, tableName, projection, selection, selectionArgs, groupBy, having, sortOrder, limit)
//                通知数据表监听器
                context?.contentResolver?.notifyChange(uri, null)
                return cursor
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("query error: " + e.message)
        }
        return null
    }

    /**
     * 更新
     */
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        try {
            val dbName = UriHandler.getDbNameFromUri(uri)
            val tableName = UriHandler.getTableNameFromUri(uri)

//            获取Uri携带的额外参数
            val conflictStrategy = CommonUtil
                .transformStringToInt(UriHandler.getParmFromUri(uri, CONFLICT_STRATEGY))
                .apply {
                    if(this == -1) CONFLICT_NONE
                }

            if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                val db = DbManager.getSQLiteDb(dbName)
                val rowCount = db.updateWithOnConflict(tableName, values, selection, selectionArgs, conflictStrategy)
//                通知数据表监听器
                context?.let {
                    if(rowCount > 0){
                        it.contentResolver.notifyChange(uri, null)
                    }
                }
                return rowCount
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("update error: " + e.message)
        }
        return 0
    }

    /**
     * 删除
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        try {
            val dbName = UriHandler.getDbNameFromUri(uri)
            val tableName = UriHandler.getTableNameFromUri(uri)

            if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                val db = DbManager.getSQLiteDb(dbName)
                val rowCount = db.delete(tableName, selection, selectionArgs)
//                通知数据表监听器
                context?.let {
                    if(rowCount > 0){
                        it.contentResolver.notifyChange(uri, null)
                    }
                }
                return rowCount
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("delete error: " + e.message)
        }
        return 0
    }

    /**
     * 自定义调用
     */
    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
//        调用当前ContentProvider所在进程的数据库管理类获取数据表操作者对象
//        在多进程情况下只需要保留一个数据库管理类即可
        if(METHOD_GET_TABLE_OPERATOR == method){
            extras?.let {
                val dbName = it.getString(DB_NAME)?: ""
                val tableName = it.getString(TABLE_NAME)?: ""
                if(!TextUtils.isEmpty(dbName) && !TextUtils.isEmpty(tableName)){
                    val tableOperator = DbManager.getTableOperator(dbName, tableName)
                    val bundle = Bundle()
                    bundle.putParcelable(METHOD_GET_TABLE_OPERATOR, tableOperator)
                    return bundle
                }
            }
        }
        return super.call(method, arg, extras)
    }

    override fun getType(uri: Uri): String = if(mUriMatcher?.match(uri) == OPERATION_CODE) UriHandler.getTableNameFromUri(uri) else ""
}