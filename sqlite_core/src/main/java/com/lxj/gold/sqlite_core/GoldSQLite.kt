package com.lxj.gold.sqlite_core

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.builder.DeleteBuilder
import com.lxj.gold.sqlite_core.dao.crud.builder.InsertBuilder
import com.lxj.gold.sqlite_core.dao.crud.builder.QueryBuilder
import com.lxj.gold.sqlite_core.dao.crud.builder.UpdateBuilder
import com.lxj.gold.sqlite_core.dao.crud.operation.ABaseDbOperation
import com.lxj.gold.sqlite_core.dao.crud.provider.SQLiteObserver
import com.lxj.gold.sqlite_core.db.DbManager
import com.lxj.gold.sqlite_core.db.DbStateListener
import com.lxj.gold.sqlite_core.db.TableHelper
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_AUTHORITY
import com.lxj.gold.sqlite_core.utils.ConstantManagerUtil.OPERATION_SCHEME

/**
 * Created by lixinjie on 2019/8/2
 * 数据库框架总的入口操作类
 */
object GoldSQLite {
    private var mContext: Application? = null
//    主线程的Handler
    private val mMainThreadHandler: Handler by lazy { Handler(Looper.getMainLooper()) }
//    数据库升级更新监听器
    private var mDbStateListener: DbStateListener? = null

    /**
     * 初始化
     * @param application   App级别的上下文
     */
    fun init(application: Application) {
        mContext = application
    }

    /**
     * 获取Context上下文对象
     */
    fun getContext(): Application = mContext!!

    /**
     * 获取主线程Handler，用于线程切换
     */
    fun getMainThreadHandler(): Handler = mMainThreadHandler

    /**
     * 获取插入操作Builder对象
     */
    fun getInsertOperation(dbName: String, tableName: String) = InsertBuilder(dbName, tableName)

    /**
     * 获取查询操作对象
     * @param classType     查询对应的数据表实体类类型
     * @param <T>           查询对应数据表实体类泛型声明
     */
    fun <T> getQueryOperation(dbName: String, classType: Class<T>) = QueryBuilder(dbName, classType)

    /**
     * 获取更新操作Builder对象
     */
    fun getUpdateOperation(dbName: String, tableName: String) = UpdateBuilder(dbName, tableName)

    /**
     * 获取删除操作Builder对象
     */
    fun getDeleteOperation(dbName: String, tableName: String) = DeleteBuilder(dbName, tableName)

    /**
     * 注册数据库状态监听器
     * @param listener
     */
    fun resgisterDbListener(listener: DbStateListener) {
        mDbStateListener = listener
    }

    /**
     * 注册观察者
     * @param observer  数据表观察者对象
     */
    fun resgisterObserver(observer: SQLiteObserver) {
        mContext?.run {
            contentResolver.registerContentObserver(Uri.parse(OPERATION_SCHEME + OPERATION_AUTHORITY), true, observer)
        }
    }

    /**
     * 注销观察者
     * @param observer  数据表观察者对象
     */
    fun ungisterObserver(observer: SQLiteObserver) {
        mContext?.run {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    /**
     * 获取数据库状态监听器
     */
    fun getDbStateListener() = mDbStateListener

    /**
     * 根据数据库名称获取数据库对象
     * @param dbName    数据库名称
     * @return          名称对应的数据库对象
     */
    fun getSQLiteDb(dbName: String) = DbManager.getSQLiteDb(dbName)

    /**
     * 删除数据表
     * @param db           数据库对象
     * @param tableName    数据表名称
     */
    fun dropTable(db: SQLiteDatabase, tableName: String) {
        TableHelper.dropTable(db, tableName)
    }
}