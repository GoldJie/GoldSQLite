package com.lxj.gold.sqlite_core.dao.crud.operation

import android.util.Log
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.builder.InsertBuilder
import com.lxj.gold.sqlite_core.dao.crud.provider.UriHandler
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException

/**
 * Created by lixinjie on 2019/9/2
 * 插入操作
 * @param tableOperator     数据表操作者
 * @param mInsertBuilder    插入操作的Builder对象
 */
class InsertOperation(tableOperator: TableOperator, private val mInsertBuilder: InsertBuilder)
    : ABaseDbOperation(tableOperator){
//    执行操作后的回调
    private var mCallback: OnDaoFinishedCallback<Int>? = null

    override fun doExecute() {
//        执行插入操作
        if (mInsertBuilder.isBulkInsert) {
//            多行插入
            bulkInsert()
        } else {
//            单行插入
            insert()
        }
    }

    /**
     * 执行插入操作不带回调
     */
    fun execute(){
        execute(null)
    }

    /**
     * 执行插入操作带回调
     * @param callback  回调
     */
    fun execute(callback: OnDaoFinishedCallback<Int>?){
//        设置回调
        mCallback = callback
//        线程池执行插入操作
        if(proTableOperator.mTableName == mInsertBuilder.tableName){
            proTableOperator.execute(this)
        }
    }

    /**
     * 插入数据
     */
    private fun insert(){
        try {
            synchronized(proTableOperator){
                Log.d(TAG, "start insert.")
                GoldSQLite.getContext()
                    .contentResolver
                    .insert(mInsertBuilder.operationUri!!, mInsertBuilder.contentValues)
                    ?.run {
//                        从插入返回的Uri中获取行Id
                        val rowId = UriHandler.getRowIdFromInsertUri(this)
//                        插入结果回调
                        onCallback(rowId)
                        Log.d(TAG, "insert end: $rowId")
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("insert error: " + e.message)
        }
    }

    /**
     * 多行插入数据
     */
    private fun bulkInsert(){
        try {
            synchronized(proTableOperator){
                Log.d(TAG, "start bulk insert.")
//                获取多行插入的ContentValues数据列表
                mInsertBuilder.contentValuesList?.let {
                    if(it.isNotEmpty()){
//                        将List转换为数组
                        val contentValues = it.toTypedArray()
                        GoldSQLite.getContext()
                            .contentResolver
                            .bulkInsert(mInsertBuilder.operationUri!!, contentValues)
                            .run {
//                                插入结果回调
                                onCallback(this)
                                Log.d(TAG, "bulk insert end: $this")
                            }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("bulkInsert error: " + e.message)
        }
    }

    /**
     * 数据操作执行结果回调
     * @param result    插入返回行Id（行数）
     */
    private fun onCallback(result: Int){
        mCallback?.let {
//            切换主线程
            GoldSQLite.getMainThreadHandler().post{
                it.onResultReturn(result)
            }
        }
    }
}