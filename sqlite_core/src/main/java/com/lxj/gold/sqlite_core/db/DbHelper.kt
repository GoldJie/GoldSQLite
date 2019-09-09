package com.lxj.gold.sqlite_core.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.parser.model.DbModel

/**
 * Created by lixinjie on 2019/8/1
 * 数据库辅助类
 */
class DbHelper(mContext: Context, dbName: String, dbVersion: Int, private val mDbModel: DbModel) :
    SQLiteOpenHelper(mContext, dbName, null, dbVersion) {
    private val TAG = DbHelper::class.java.simpleName

    /**
     * 初始版本数据库的创建
     * @param sqLiteDatabase    数据库对象
     */
    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        Log.d(TAG, "database onCreate")
        sqLiteDatabase?.let {
            TableHelper.createTables(it, mDbModel)
        }
//        数据库创建监听
        GoldSQLite.getDbStateListener()?.run {
            onCreate(mDbModel.getDbName()?: "")
        }
    }

    /**
     * 后续版本数据库的更新
     * @param sqLiteDatabase    数据库对象
     * @param oldVersion        数据库旧版本号
     * @param newVersion        数据库新版本号
     */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "database update to version: $newVersion")
        sqLiteDatabase?.let {
            TableHelper.upgrateTables(sqLiteDatabase, mDbModel)
        }
    }
}