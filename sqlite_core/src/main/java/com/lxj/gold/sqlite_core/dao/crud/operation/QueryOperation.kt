package com.lxj.gold.sqlite_core.dao.crud.operation

import android.database.Cursor
import android.util.Log
import com.lxj.gold.sqlite_core.GoldSQLite
import com.lxj.gold.sqlite_core.dao.TableOperator
import com.lxj.gold.sqlite_core.dao.crud.builder.QueryBuilder
import com.lxj.gold.sqlite_core.db.TableHelper
import com.lxj.gold.sqlite_core.exception.GoldSQLiteCommonException
import com.lxj.gold.sqlite_core.utils.CommonUtil
import java.util.ArrayList

/**
 * Created by lixinjie on 2019/9/3
 * 查询操作
 * @param tableOperator     数据表操作者
 * @param mQueryBuilder     查询操作的Builder类
 */
class QueryOperation(tableOperator: TableOperator, private val mQueryBuilder: QueryBuilder<*>)
    : ABaseDbOperation(tableOperator){
//    执行操作后的回调
    private var mCallback: OnDaoFinishedCallback<Any>? = null

    override fun doExecute() {
//        执行查询操作
        query()
    }

    /**
     * 执行查询操作不带回调
     */
    fun execute() {
        execute(null)
    }

    /**
     * 执行查询操作带回调
     * @param callback  回调
     */
    fun execute(callback: OnDaoFinishedCallback<Any>?){
//        设置回调
        mCallback = callback
//        线程池执行查询操作
        if(proTableOperator.mTableName == mQueryBuilder.tableName){
            proTableOperator.execute(this)
        }
    }

    /**
     * 查询数据
     */
    private fun query(){
       var cursor: Cursor? = null
        try {
            synchronized(proTableOperator){
                Log.d(TAG, "start query.")
                val resultList = ArrayList<Any>()
                cursor = GoldSQLite.getContext()
                    .contentResolver
                    .query(mQueryBuilder.operationUri!!, mQueryBuilder.columns,
                        mQueryBuilder.selection, mQueryBuilder.selectionArgs,
                        mQueryBuilder.orderBy)
                cursor?.run {
                    while (this.moveToNext()){
                        mQueryBuilder.tableClass?.let {
                            TableHelper.transformCursorToEntity(this, it)
                        }?.let {
                            resultList.add(it)
                        }
                    }
                    Log.d(TAG, "query end: " + resultList.size)
                }
//                查询结果回调
                onCallback(resultList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("query error: " + e.message)
        }finally {
            CommonUtil.closeCursor(cursor)
        }
    }

    /**
     * 数据操作执行结果回调
     * @param result    查询的数据列表
     */
    private fun onCallback(result: List<Any>){
        try {
            mCallback?.let {
//                切换主线程
                GoldSQLite.getMainThreadHandler().post{
                    it.onResultReturn(result)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw GoldSQLiteCommonException("query onCallback error: " + e.message)
        }
    }
}