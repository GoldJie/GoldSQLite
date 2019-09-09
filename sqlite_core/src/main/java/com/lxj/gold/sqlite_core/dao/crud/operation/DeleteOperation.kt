package com.lxj.gold.sqlite_core.dao.crud.operation

import android.util.Log
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.builder.DeleteBuilder
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException

/**
 * Created by lixinjie on 2019/8/30
 * 删除操作
 * @param tableOperator     数据表操作者
 * @param mDeleteBuilder    删除操作的Builder对象
 */
class DeleteOperation(tableOperator: TableOperator, private val mDeleteBuilder: DeleteBuilder)
    : ABaseDbOperation(tableOperator){
//    执行操作后的回调
    private var mCallback: OnDaoFinishedCallback<Int>? = null

    override fun doExecute() {
//        执行删除操作
        delete()
    }

    /**
     * 执行删除操作不带回调
     */
    fun execute(){
        execute(null)
    }

    /**
     * 执行删除操作带回调
     * @param callback  回调
     */
    fun execute(callback: OnDaoFinishedCallback<Int>?){
//        设置回调
        mCallback = callback
//        线程池执行删除操作
        if(proTableOperator.tableName == mDeleteBuilder.tableName){
            proTableOperator.execute(this)
        }
    }

    /**
     * 删除数据
     */
    private fun delete(){
        try {
            synchronized(proTableOperator){
                Log.d(TAG, "start delete.")
                GoldSQLite.getContext()
                    .contentResolver
                    .delete(mDeleteBuilder.operationUri!!, mDeleteBuilder.whereClause, mDeleteBuilder.whereArgs)
                    .run {
//                        删除结果回调
                        onCallback(this)
                        Log.d(TAG, "delete end: $this")
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("delete error: " + e.message)
        }
    }

    /**
     * 数据操作执行结果回调
     * @param result    删除的行数
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