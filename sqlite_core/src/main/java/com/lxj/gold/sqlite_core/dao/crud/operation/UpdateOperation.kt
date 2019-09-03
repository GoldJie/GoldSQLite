package com.lxj.gold.sqlite_core.dao.crud.operation

import android.util.Log
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.builder.UpdateBuilder
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException

/**
 * Created by lixinjie on 2019/8/31
 * 更新操作
 * @param tableOperator     数据表操作者
 * @param mUpdateBuilder    更新操作的Builder类
 */
class UpdateOperation(tableOperator: TableOperator, private val mUpdateBuilder: UpdateBuilder)
    : ABaseDbOperation(tableOperator) {
//    执行操作后的回调
    private var mCallback: OnDaoFinishedCallback<Int>? = null

    override fun doExecute() {
//        执行更新操作
        update()
    }

    /**
     * 执行更新操作不带回调
     */
    fun execute(){
        execute(null)
    }

    /**
     * 执行更新操作带回调
     * @param callback  回调
     */
    fun execute(callback: OnDaoFinishedCallback<Int>?){
//        设置回调
        mCallback = callback
//        线程池执行查询操作
        if(proTableOperator.mTableName == mUpdateBuilder.tableName){
            proTableOperator.execute(this)
        }
    }

    /**
     * 更新数据
     */
    private fun update(){
        try {
            synchronized(proTableOperator){
                Log.d(TAG, "start update.")
                GoldSQLite.getContext()
                    .contentResolver
                    .update(mUpdateBuilder.operationUri!!, mUpdateBuilder.contentValues,
                        mUpdateBuilder.whereClause, mUpdateBuilder.whereArgs)
                    .run {
//                        更新结果回调
                        onCallback(this)
                        Log.d(TAG, "update end: $this")
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("update error: " + e.message)
        }
    }

    /**
     * 数据操作执行结果回调
     * @param result    更新返回行数
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