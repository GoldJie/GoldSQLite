package com.lxj.gold.sqlite_core.dao.crud.provider

import android.database.ContentObserver
import android.net.Uri
import android.text.TextUtils
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.utils.CommonUtil

/**
 * Created by lixinjie on 2019/8/28
 * 数据库观察者
 * @param mTableName        数据表名
 * @param mOperationtype    操作类型
 */
abstract class SQLiteObserver(private var mTableName: String?, private var mOperationtype: Int)
    : ContentObserver(GoldSQLite.getMainThreadHandler()){

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        uri?.let {
            val tableName = UriHandler.getTableNameFromUri(it)
            val operationType = CommonUtil.transformStringToInt(it.fragment)
            if(!TextUtils.isEmpty(mTableName) && mOperationtype != 0){
                if(mTableName != tableName && mOperationtype != operationType) return
            }
            if(!TextUtils.isEmpty(mTableName)){
                if(mTableName != tableName) return
            }
            if(mOperationtype != 0){
                if(mOperationtype != operationType) return
            }
            onChange(tableName, operationType)
        }
    }

    /**
     * 数据表数据变更
     * @param tableName         数据表名
     * @param operationType     数据表操作类型
     */
    abstract fun onChange(tableName: String, operationType: Int)
}